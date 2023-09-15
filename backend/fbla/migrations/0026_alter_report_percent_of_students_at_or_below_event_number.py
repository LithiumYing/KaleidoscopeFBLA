# Generated by Django 4.1.4 on 2023-06-25 23:34

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0025_alter_report_percent_of_students_at_or_below_event_number"),
    ]

    operations = [
        migrations.AlterField(
            model_name="report",
            name="percent_of_students_at_or_below_event_number",
            field=models.PositiveSmallIntegerField(
                blank=True,
                null=True,
                verbose_name="Show percentage of students who attended fewer than this many events",
            ),
        ),
    ]
