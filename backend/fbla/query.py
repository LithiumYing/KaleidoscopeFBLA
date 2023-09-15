import graphene
from .models import (
    Event,
    Prize,
    Student,
    StudentEventRelation,
    AvailableStudent,
    BonusPoints,
)
from graphene_django import DjangoObjectType
from .utils import year_grade_bidict, url_from_image_field
from django.db.models.functions import Random
from bidict import bidict
from random import choice
from graphql_jwt.decorators import login_required
from typing import Optional
from django.contrib.auth.models import User
from graphql_jwt.shortcuts import get_user_by_token


class GradePoints(graphene.ObjectType):
    freshman = graphene.Int(required=True)
    sophomore = graphene.Int(required=True)
    junior = graphene.Int(required=True)
    senior = graphene.Int(required=True)


class EventType(DjangoObjectType):
    class Meta:
        model = Event
        fields = [
            "id",
            "name",
            "location",
            "start_time",
            "end_time",
            "attendees",
            "is_recurring",
            "total_count",
            "past_count",
            "points",
            "elapsed",
            "relapse_time",
        ]
        ordering = ["name"]

    checkin_open = graphene.Boolean(required=True)
    checkout_open = graphene.Boolean(required=True)
    image = graphene.String(required=False)

    def resolve_checkin_open(self, info):
        return self.is_checkin_open()

    def resolve_checkout_open(self, info):
        return self.is_checkout_open()

    def resolve_image(self, info) -> Optional[str]:
        return url_from_image_field(self.image_field) if self.image_field else None


class AvailableStudentType(DjangoObjectType):
    class Meta:
        model = AvailableStudent
        fields = ["student_id", "last_name", "first_name", "graduation_year"]
        ordering = ["student_id"]


class BonusPointsType(DjangoObjectType):
    class Meta:
        model = BonusPoints
        fields = ["id", "student", "points", "reason", "date_issued"]
        ordering = ["date_issued", "-points"]


class PrizeType(DjangoObjectType):
    class Meta:
        model = Prize
        fields = ["id", "name", "description", "student"]
        ordering = ["name"]

    image = graphene.String(required=False)

    def resolve_image(self, info) -> Optional[str]:
        return url_from_image_field(self.image_field) if self.image_field else None


class StudentType(DjangoObjectType):
    class Meta:
        model = Student
        fields = ["student_id", "last_name", "first_name", "graduation_year", "rank"]
        ordering = ["-points", "student_id"]

    email = graphene.String(required=True)
    qrcode = graphene.String(required=True)
    points = graphene.Int(required=True)
    events_attended = graphene.Int(required=True)
    bonus_points = graphene.List(BonusPointsType, required=True)
    prizes = graphene.List(PrizeType, required=True)

    def resolve_email(self, info):
        return self.user.email

    def resolve_qrcode(self, info):
        return self.qr_code()

    def resolve_points(self, info):
        return self.get_points()

    def resolve_events_attended(self, info):
        return self.event_set.filter(elapsed=True, active_points=True).count()

    def resolve_bonus_points(self, info):
        return BonusPoints.objects.filter(student__student_id=self.student_id)

    def resolve_prizes(self, info):
        return Prize.objects.filter(student__student_id=self.student_id)


class Query(graphene.ObjectType):
    student_by_id = graphene.Field(StudentType, student_id=graphene.Int())
    event_by_id = graphene.Field(EventType, id=graphene.ID())
    prize_by_id = graphene.Field(PrizeType, id=graphene.ID())
    grade_by_graduation_year = graphene.Field(
        graphene.String, graduation_year=graphene.Int()
    )
    graduation_year_by_grade = graphene.Field(graphene.Int, grade=graphene.String())
    events_by_location = graphene.List(EventType, location=graphene.String())
    events_by_student = graphene.List(EventType, student_id=graphene.Int())
    students_by_event_id = graphene.List(StudentType, event_id=graphene.Int())
    student_by_username = graphene.Field(StudentType, email=graphene.String())
    events = graphene.List(EventType)
    students = graphene.List(StudentType)
    prizes = graphene.List(PrizeType)
    available_prizes = graphene.List(PrizeType)
    available_events = graphene.List(EventType)
    grade_points = graphene.Field(GradePoints)
    random_student_by_graduation_year = graphene.Field(
        StudentType, graduation_year=graphene.Int()
    )
    student_with_max_points_by_graduation_year = graphene.Field(
        StudentType, graduation_year=graphene.Int()
    )
    validate_staff_by_credentials = graphene.Boolean(
        email=graphene.String(), password=graphene.String()
    )
    student_by_token = graphene.Field(StudentType, token=graphene.String())
    available_student_by_student_id = graphene.Field(
        AvailableStudentType, student_id=graphene.Int()
    )
    available_students = graphene.List(AvailableStudentType)
    search_events_by_name = graphene.List(EventType, name=graphene.String())

    @login_required
    def resolve_events(self, info):
        return Event.objects.all()

    @login_required
    def resolve_students(self, info):
        return Student.objects.all()

    @login_required
    def resolve_prizes(self, info):
        return Prize.objects.all()

    @login_required
    def resolve_grade_points(self, info):
        points_dict = {"freshman": 0, "sophomore": 0, "junior": 0, "senior": 0}
        grade_dict = year_grade_bidict()  # to save op time
        for student in Student.objects.all():
            points_dict[
                self.resolve_grade_by_graduation_year(
                    info, student.graduation_year, grade_dict=grade_dict
                ).casefold()
            ] += student.get_points()
        return GradePoints(**points_dict)

    @login_required
    def resolve_events_by_student(self, info, student_id):
        return Event.objects.filter(attendees__student_id=student_id)

    @login_required
    def resolve_events_by_location(self, info, location):
        return Event.objects.filter(location=location)

    @login_required
    def resolve_student_by_id(self, info, student_id):
        return Student.objects.get(student_id=student_id)

    @login_required
    def resolve_grade_by_graduation_year(
        self, info, graduation_year, *, grade_dict: Optional[bidict[int, str]] = None
    ) -> str:
        if grade_dict is None:
            grade_dict = year_grade_bidict()
        if graduation_year in grade_dict:
            return grade_dict[graduation_year]
        else:
            return "Invalid"

    @login_required
    def resolve_graduation_year_by_grade(
        self, info, grade, *, grade_dict: Optional[bidict[str, int]] = None
    ) -> int:
        if grade_dict is None:
            grade_dict = year_grade_bidict().inverse
        if grade in grade_dict:
            return grade_dict[grade]
        else:
            return -1

    @login_required
    def resolve_random_student_by_graduation_year(self, info, graduation_year):
        return choice(
            Student.objects.filter(
                graduation_year=graduation_year,
                studenteventrelation__in=StudentEventRelation.objects.filter(
                    checked_out=True
                ),
            )
        )

    @login_required
    def resolve_student_with_max_points_by_graduation_year(
        self, info, graduation_year
    ) -> Student:
        return (
            Student.objects.filter(graduation_year=graduation_year)
            .annotate(random_num=Random())
            .order_by("-points", "random_num")[0]
        )

    @login_required
    def resolve_student_by_username(self, info, email):
        return Student.objects.get(user__username=email)

    @login_required
    def resolve_event_by_id(self, info, id):
        return Event.objects.get(id=id)

    @login_required
    def resolve_prize_by_id(self, info, id):
        return Prize.objects.get(id=id)

    @login_required
    def resolve_students_by_event_id(self, info, event_id):
        return Student.objects.filter(events__id=event_id)

    def resolve_validate_staff_by_credentials(self, info, email, password) -> bool:
        user = User.objects.get(username=email)
        return user and user.check_password(password) and user.is_staff

    def resolve_student_by_token(self, info, token):
        try:
            user = get_user_by_token(token[4:])
        except:
            raise Exception("token not linked to user")
        try:
            return Student.objects.get(user=user)
        except:
            raise Exception("user not linked to student")

    def resolve_available_student_by_student_id(self, info, student_id):
        return AvailableStudent.objects.get(student_id=student_id)

    @login_required
    def resolve_available_students(self, info):
        return AvailableStudent.objects.all()

    @login_required
    def resolve_search_events_by_name(self, info, name):
        return Event.objects.filter(name__icontains=name)

    @login_required
    def resolve_available_prizes(self, info):
        return Prize.objects.filter(student__isnull=True)

    @login_required
    def resolve_available_events(self, info):
        return Event.objects.filter(elapsed=False)
