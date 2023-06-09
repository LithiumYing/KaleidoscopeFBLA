# Generated by Django 4.1.4 on 2023-04-12 02:21

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0015_alter_report_report"),
    ]

    operations = [
        migrations.AddField(
            model_name="event",
            name="always_checkin_out_able",
            field=models.BooleanField(
                default=False, verbose_name="Always can Check In/Out"
            ),
        ),
        migrations.AlterField(
            model_name="event",
            name="past_count",
            field=models.PositiveSmallIntegerField(
                blank=True,
                null=True,
                verbose_name="How many times has this event occurred so far?",
            ),
        ),
        migrations.AlterField(
            model_name="event",
            name="relapse_time",
            field=models.PositiveBigIntegerField(
                blank=True,
                null=True,
                verbose_name="How many seconds in between each event?",
            ),
        ),
        migrations.AlterField(
            model_name="event",
            name="total_count",
            field=models.PositiveSmallIntegerField(
                blank=True,
                null=True,
                verbose_name="How many times will this event occur in total?",
            ),
        ),
    ]
