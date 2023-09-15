from django.db import models
from datetime import timedelta
from django.contrib.auth.models import User
import qrcode
from qrcode.constants import ERROR_CORRECT_H
from PIL import Image
from io import BytesIO
import base64
from django.core.files.storage import default_storage
from django.db.models.signals import post_save
from django.dispatch import receiver
from django.utils.timezone import now
from .utils import (
    get_school_year,
    image_upload_path,
    report_upload_path,
    backup_upload_path,
)
from django.core.exceptions import ValidationError


class AvailableStudent(models.Model):
    student_id = models.PositiveIntegerField(primary_key=True)
    last_name = models.CharField(max_length=32)
    first_name = models.CharField(max_length=32)
    graduation_year = models.PositiveSmallIntegerField()

    class Meta:
        verbose_name = "Available Student"
        verbose_name_plural = "Available Students"
        ordering = ["last_name"]

    def __str__(self):
        return f"{self.last_name}, {self.first_name} - {self.student_id}"


class Report(models.Model):
    report = models.FileField(
        upload_to=report_upload_path,
        verbose_name="Report File",
        editable=True,
        blank=True,
    )
    year = models.PositiveSmallIntegerField(editable=False, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    graphs = models.BooleanField(
        default=True, verbose_name="Show data distributions by grade"
    )
    statistics = models.BooleanField(
        default=True, verbose_name="Show statistics of student point data by grade"
    )
    winners = models.BooleanField(default=True, verbose_name="Show winners of prizes")
    breakdown = models.BooleanField(
        default=True, verbose_name="Show in-depth breakdown of student points"
    )
    percent_of_students_at_or_below_event_number = models.PositiveSmallIntegerField(
        blank=True,
        null=True,
        verbose_name=r"Show % of students with <X events attended.",
    )

    class Meta:
        verbose_name = "Report"
        verbose_name_plural = "Reports"

    def save(self, *args, **kwargs):
        if not self.year:
            self.year = get_school_year()
        if not self.report:
            from . import report

            report.generate_report(
                graphs=self.graphs if self.graphs is not None else True,
                winners=self.winners if self.winners is not None else True,
                breakdown=self.breakdown if self.breakdown is not None else True,
                statistics=self.statistics if self.statistics is not None else True,
                percent_of_students_at_or_below_event_number=self.percent_of_students_at_or_below_event_number,
            )
            return

        super().save(*args, **kwargs)

    def delete(self, *args, **kwargs):
        default_storage.delete(self.report.name)
        super().delete(*args, **kwargs)

    def __str__(self) -> str:
        return self.report.name.replace("reports/", "")


class BonusPoints(models.Model):
    student = models.ForeignKey("Student", on_delete=models.CASCADE)
    reason = models.CharField(max_length=256)
    points = models.PositiveIntegerField()
    date_issued = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "Bonus Points"
        verbose_name_plural = "Bonus Points Instances"
        ordering = ["date_issued", "-points"]

    def __str__(self):
        return f"{self.student} - {self.points} points"


class Student(models.Model):
    student_id = models.PositiveIntegerField(verbose_name="ID Number", primary_key=True)
    last_name = models.CharField(max_length=32, verbose_name="Last Name")
    first_name = models.CharField(max_length=32, verbose_name="First Name")
    graduation_year = models.PositiveSmallIntegerField(
        default=2025, verbose_name="Graduation Year"
    )
    user = models.OneToOneField(
        User, on_delete=models.CASCADE, verbose_name="Linked User"
    )
    points = models.PositiveIntegerField(verbose_name="Points", default=0)
    rank = models.PositiveIntegerField(verbose_name="Rank", default=0)

    class Meta:
        verbose_name = "Student"
        verbose_name_plural = "Students"
        ordering = ["-points", "last_name"]
        constraints = [
            models.UniqueConstraint(fields=["student_id"], name="unique_student_id"),
            models.UniqueConstraint(fields=["user"], name="unique_user"),
        ]

    def qr_code(self, width=144) -> str:
        if (
            self.student_id == self.cached_qr_code[0]
            and self.cached_qr_code[1] is not None
        ):
            return self.cached_qr_code[1]
        module_count = int(width / 12)
        qr = qrcode.QRCode(
            version=None,
            error_correction=ERROR_CORRECT_H,
            box_size=module_count,
            border=2,
        )
        qr.add_data(self.student_id, optimize=0)
        qr.make(fit=True)
        qr_image = qr.make_image(fill_color="black", back_color="white")
        qr_image = qr_image.resize((width, width), resample=Image.Resampling.NEAREST)
        png_buffer = BytesIO()
        qr_image.save(png_buffer, format="PNG")
        png_base64 = base64.b64encode(png_buffer.getvalue()).decode("utf-8")
        self.cached_qr_code = (self.student_id, png_base64)
        return png_base64

    def get_points(self):
        return sum(
            [
                event.points if event.active_points and event.elapsed else 0
                for event in self.event_set.all()
            ]
        ) + sum([bonus.points for bonus in BonusPoints.objects.filter(student=self)])

    @classmethod
    def update_points_fields_and_ranks(cls):
        previous_student = None
        students = []
        for i, student in enumerate(cls.objects.all(), start=1):
            student.points = student.get_points()
            student.rank = (
                previous_student.rank
                if previous_student is not None
                and previous_student.points == student.points
                else i
            )
            students.append(student)
            previous_student = student
        cls.objects.bulk_update(students, ["points", "rank"])

    def __int__(self):
        return self.student_id

    def __str__(self):
        return f"{self.last_name}, {self.first_name} - {self.student_id}"

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.cached_qr_code = (self.student_id, None)


class Event(models.Model):
    name = models.CharField(max_length=32)
    location = models.CharField(max_length=32)
    start_time = models.DateTimeField()
    end_time = models.DateTimeField()
    attendees = models.ManyToManyField(
        Student, blank=True, verbose_name="Attendees", through="StudentEventRelation"
    )
    points = models.PositiveIntegerField(blank=True, null=False)
    elapsed = models.BooleanField(default=False)
    active_points = models.BooleanField(default=True)
    image_field = models.ImageField(
        upload_to=image_upload_path, blank=True, null=True, verbose_name="Image"
    )

    # recurring stuff
    is_recurring = models.BooleanField(
        default=False, verbose_name="Does this event recur?"
    )
    relapse_time = models.PositiveBigIntegerField(
        blank=True, null=True, verbose_name="How many days in between each event?"
    )
    total_count = models.PositiveSmallIntegerField(
        blank=True,
        null=True,
        verbose_name="How many times will this event occur in total?",
    )
    past_count = models.PositiveSmallIntegerField(
        blank=True,
        null=True,
        verbose_name="How many times has this event occurred so far?",
    )
    always_checkin_out_able = models.BooleanField(
        default=False, verbose_name="Always can Check In/Out"
    )

    class Meta:
        verbose_name = "Event"
        verbose_name_plural = "Events"
        ordering = ["start_time", "name"]

    def is_checkin_open(self):
        return (
            (self.start_time - timedelta(minutes=5))
            < now()
            < (
                self.start_time
                + timedelta(seconds=(self.end_time - self.start_time).seconds / 4)
            )
        ) or self.always_checkin_out_able

    def is_checkout_open(self):
        return (
            (
                self.start_time
                + timedelta(seconds=(self.end_time - self.start_time).seconds / 1.5)
            )
            < now()
            < (
                self.start_time
                + (self.end_time - self.start_time)
                + timedelta(minutes=15)
            )
        ) or self.always_checkin_out_able

    def get_points(self) -> int:
        return int(
            round((self.end_time - self.start_time).seconds * 0.1388, -1)
        )  # 1 hour is 500 points

    def end_event(self) -> bool:
        try:
            if not self.elapsed:
                for student in self.attendees.filter(
                    studenteventrelation__checked_out=False
                ):
                    self.attendees.remove(student)
                if not self.is_recurring:
                    self.elapsed = True
                elif self.total_count is not None and self.past_count is not None:
                    if self.past_count < self.total_count:
                        self.past_count += 1
                        self.save()
                        return False
                    else:
                        self.elapsed = True
                else:
                    raise Exception(
                        "Event is recurring but total_count or past_count is None"
                    )
                self.save()
                return True
            return False
        finally:
            Student.update_points_fields_and_ranks()

    def next_event(self):
        if (
            self.is_recurring == True
            and self.relapse_time is not None
            and self.past_count is not None
        ):
            return self.start_time + timedelta(days=self.relapse_time * self.past_count)
        else:
            return None

    def save(self, *args, **kwargs):
        points = self.get_points()
        if self.points != points:
            self.points = points
        if self.is_recurring:
            if self.total_count is None:
                self.total_count = 0
            if self.past_count is None:
                self.past_count = 0
            if self.relapse_time is None:
                raise ValidationError(
                    "Relapse time cannot be None if event is recurring"
                )
        if self.end_time < self.start_time:
            raise ValidationError("End time cannot be before start time")
        super(Event, self).save(*args, **kwargs)

    def delete(self, *args, **kwargs):
        # If an image was uploaded, delete it from the storage
        if self.image_field:
            default_storage.delete(self.image_field.name)
        super().delete(*args, **kwargs)

    def __str__(self):
        return self.name


class StudentEventRelation(models.Model):
    student = models.ForeignKey(Student, on_delete=models.CASCADE)
    event = models.ForeignKey(Event, on_delete=models.CASCADE)
    checked_out = models.BooleanField(default=False)

    class Meta:
        verbose_name = "Add Student to Event"
        verbose_name_plural = "Add Student to Event"
        ordering = ["student", "event"]

    def __str__(self):
        return f"{self.student} - {self.event}"


class Prize(models.Model):
    name = models.CharField(max_length=32)
    description = models.CharField(max_length=256)
    image_field = models.ImageField(
        upload_to=image_upload_path, blank=True, null=True, verbose_name="Image"
    )
    student = models.ForeignKey(
        Student, on_delete=models.SET_NULL, blank=True, null=True
    )

    class Meta:
        verbose_name = "Prize"
        verbose_name_plural = "Prizes"
        ordering = ["name"]

    @classmethod
    def reset_prizes(cls):
        Prize.objects.filter(student__isnull=False).update(student=None)

    def delete(self, *args, **kwargs):
        # If an image was uploaded, delete it from the storage
        if self.image_field:
            default_storage.delete(self.image_field.name)
        super().delete(*args, **kwargs)

    def __str__(self):
        return self.name


class Backup(models.Model):
    created_at = models.DateTimeField(auto_now_add=True)
    file = models.FileField(upload_to=backup_upload_path, verbose_name="Backup File")

    def __str__(self):
        return self.file.name.replace("backups/", "")


@receiver(post_save, sender=(BonusPoints, Event, Student, StudentEventRelation))
def update_points_fields_and_ranks(sender, **kwargs):
    Student.update_points_fields_and_ranks()
