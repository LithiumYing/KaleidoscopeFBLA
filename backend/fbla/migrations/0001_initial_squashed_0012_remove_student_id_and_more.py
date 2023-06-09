# Generated by Django 4.1.7 on 2023-04-08 03:11

from django.conf import settings
from django.db import migrations, models
import django.db.migrations.operations.special
import django.db.models.deletion
import fbla.utils


def create_through_relations(apps, schema_editor):
    Student = apps.get_model("fbla", "Student")
    Event = apps.get_model("fbla", "Event")
    StudentEventRelation = apps.get_model("fbla", "StudentEventRelation")
    for student in Student.objects.all():
        for event in Event.objects.all():
            StudentEventRelation.objects.create(student=student, event=event)


class Migration(migrations.Migration):
    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.CreateModel(
            name="AvailableStudent",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("student_id", models.PositiveIntegerField()),
                ("last_name", models.CharField(max_length=32)),
                ("first_name", models.CharField(max_length=32)),
                ("graduation_year", models.PositiveSmallIntegerField(default=2025)),
            ],
            options={
                "ordering": ["-last_name"],
            },
        ),
        migrations.CreateModel(
            name="Student",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("student_id", models.PositiveIntegerField()),
                ("last_name", models.CharField(max_length=32)),
                ("first_name", models.CharField(max_length=32)),
                ("graduation_year", models.PositiveSmallIntegerField(default=2025)),
                ("points", models.PositiveIntegerField(default=0)),
                ("events_attended", models.PositiveIntegerField(default=0)),
                (
                    "user",
                    models.OneToOneField(
                        on_delete=django.db.models.deletion.CASCADE,
                        to=settings.AUTH_USER_MODEL,
                    ),
                ),
            ],
            options={
                "ordering": ["-last_name"],
            },
        ),
        migrations.CreateModel(
            name="Prize",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("name", models.CharField(max_length=32)),
                ("description", models.CharField(max_length=256)),
                (
                    "image",
                    models.ImageField(
                        blank=True, null=True, upload_to=fbla.utils.image_upload_path
                    ),
                ),
                (
                    "student",
                    models.ForeignKey(
                        blank=True,
                        null=True,
                        on_delete=django.db.models.deletion.SET_NULL,
                        to="fbla.student",
                    ),
                ),
            ],
        ),
        migrations.CreateModel(
            name="Event",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("name", models.CharField(max_length=32)),
                ("location", models.CharField(max_length=32)),
                ("start_time", models.DateTimeField()),
                ("end_time", models.DateTimeField()),
                ("points", models.PositiveIntegerField(blank=True)),
                ("elapsed", models.BooleanField(default=False)),
                ("is_recurring", models.BooleanField(default=False)),
                ("relapse_time", models.PositiveBigIntegerField(blank=True, null=True)),
                (
                    "total_count",
                    models.PositiveSmallIntegerField(blank=True, null=True),
                ),
                ("past_count", models.PositiveSmallIntegerField(blank=True, null=True)),
                ("attendees", models.ManyToManyField(blank=True, to="fbla.student")),
            ],
            options={
                "ordering": ["start_time", "-name"],
            },
        ),
        migrations.AddConstraint(
            model_name="availablestudent",
            constraint=models.UniqueConstraint(
                fields=("student_id",), name="unique_available_student_id"
            ),
        ),
        migrations.AddConstraint(
            model_name="student",
            constraint=models.UniqueConstraint(
                fields=("student_id",), name="unique_student_id"
            ),
        ),
        migrations.AddConstraint(
            model_name="student",
            constraint=models.UniqueConstraint(fields=("user",), name="unique_user"),
        ),
        migrations.AlterModelOptions(
            name="event",
            options={"ordering": ["start_time", "name"]},
        ),
        migrations.AlterModelOptions(
            name="student",
            options={"ordering": ["last_name"]},
        ),
        migrations.RemoveField(
            model_name="student",
            name="points",
        ),
        migrations.AddField(
            model_name="event",
            name="active_points",
            field=models.BooleanField(default=True),
        ),
        migrations.AlterModelOptions(
            name="availablestudent",
            options={
                "ordering": ["last_name"],
                "verbose_name": "Available Student",
                "verbose_name_plural": "Available Students",
            },
        ),
        migrations.CreateModel(
            name="BonusPoints",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("reason", models.CharField(max_length=256)),
                ("points", models.PositiveIntegerField()),
                ("date_issued", models.DateTimeField(auto_now_add=True)),
                (
                    "student",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE, to="fbla.student"
                    ),
                ),
            ],
            options={
                "ordering": ["date_issued", "-points"],
                "verbose_name": "Bonus Points",
                "verbose_name_plural": "Bonus Points Instances",
            },
        ),
        migrations.AlterModelOptions(
            name="event",
            options={
                "ordering": ["start_time", "name"],
                "verbose_name": "Event",
                "verbose_name_plural": "Events",
            },
        ),
        migrations.AlterModelOptions(
            name="prize",
            options={
                "ordering": ["name"],
                "verbose_name": "Prize",
                "verbose_name_plural": "Prizes",
            },
        ),
        migrations.AlterModelOptions(
            name="student",
            options={
                "ordering": ["last_name"],
                "verbose_name": "Student",
                "verbose_name_plural": "Students",
            },
        ),
        migrations.AlterField(
            model_name="student",
            name="events_attended",
            field=models.PositiveIntegerField(
                default=0, verbose_name="Events Attended"
            ),
        ),
        migrations.AlterField(
            model_name="student",
            name="first_name",
            field=models.CharField(max_length=32, verbose_name="First Name"),
        ),
        migrations.AlterField(
            model_name="student",
            name="graduation_year",
            field=models.PositiveSmallIntegerField(
                default=2025, verbose_name="Graduation Year"
            ),
        ),
        migrations.AlterField(
            model_name="student",
            name="last_name",
            field=models.CharField(max_length=32, verbose_name="Last Name"),
        ),
        migrations.AlterField(
            model_name="student",
            name="student_id",
            field=models.PositiveIntegerField(verbose_name="ID Number"),
        ),
        migrations.AlterField(
            model_name="student",
            name="user",
            field=models.OneToOneField(
                on_delete=django.db.models.deletion.CASCADE,
                to=settings.AUTH_USER_MODEL,
                verbose_name="Linked User",
            ),
        ),
        migrations.AlterModelOptions(
            name="student",
            options={
                "ordering": ["-points", "last_name"],
                "verbose_name": "Student",
                "verbose_name_plural": "Students",
            },
        ),
        migrations.RemoveField(
            model_name="student",
            name="events_attended",
        ),
        migrations.AddField(
            model_name="student",
            name="points",
            field=models.PositiveIntegerField(default=0, verbose_name="Points"),
        ),
        migrations.AddField(
            model_name="student",
            name="rank",
            field=models.PositiveIntegerField(default=0, verbose_name="Rank"),
        ),
        migrations.RemoveConstraint(
            model_name="availablestudent",
            name="unique_available_student_id",
        ),
        migrations.RemoveField(
            model_name="availablestudent",
            name="id",
        ),
        migrations.AddField(
            model_name="event",
            name="image_field",
            field=models.ImageField(
                blank=True,
                null=True,
                upload_to=fbla.utils.image_upload_path,
                verbose_name="Image",
            ),
        ),
        migrations.AlterField(
            model_name="availablestudent",
            name="student_id",
            field=models.PositiveIntegerField(primary_key=True, serialize=False),
        ),
        migrations.RenameField(
            model_name="prize",
            old_name="image",
            new_name="image_field",
        ),
        migrations.AlterField(
            model_name="prize",
            name="image_field",
            field=models.ImageField(
                blank=True,
                null=True,
                upload_to=fbla.utils.image_upload_path,
                verbose_name="Image",
            ),
        ),
        migrations.AlterField(
            model_name="event",
            name="attendees",
            field=models.ManyToManyField(
                blank=True, to="fbla.student", verbose_name="Attendees"
            ),
        ),
        migrations.CreateModel(
            name="StudentEventRelation",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("checked_out", models.BooleanField(default=False)),
                (
                    "event",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE, to="fbla.event"
                    ),
                ),
                (
                    "student",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE, to="fbla.student"
                    ),
                ),
            ],
            options={
                "verbose_name": "Student Event Relation",
                "verbose_name_plural": "Student Event Relations",
                "ordering": ["student", "event"],
            },
        ),
        migrations.RunPython(
            code=create_through_relations,
            reverse_code=django.db.migrations.operations.special.RunPython.noop,
        ),
        migrations.RemoveField(
            model_name="event",
            name="attendees",
        ),
        migrations.AddField(
            model_name="event",
            name="attendees",
            field=models.ManyToManyField(
                blank=True,
                through="fbla.StudentEventRelation",
                to="fbla.student",
                verbose_name="Attendees",
            ),
        ),
        migrations.CreateModel(
            name="Report",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                (
                    "report",
                    models.FileField(
                        editable=False,
                        upload_to=fbla.utils.report_upload_path,
                        verbose_name="Report File",
                    ),
                ),
                ("year", models.PositiveSmallIntegerField(blank=True, editable=False)),
            ],
            options={
                "verbose_name": "Report",
                "verbose_name_plural": "Reports",
            },
        ),
        migrations.RemoveField(
            model_name="student",
            name="id",
        ),
        migrations.AlterField(
            model_name="availablestudent",
            name="graduation_year",
            field=models.PositiveSmallIntegerField(),
        ),
        migrations.AlterField(
            model_name="student",
            name="student_id",
            field=models.PositiveIntegerField(
                primary_key=True, serialize=False, verbose_name="ID Number"
            ),
        ),
    ]
