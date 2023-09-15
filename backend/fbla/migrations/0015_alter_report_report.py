# Generated by Django 4.2 on 2023-04-09 03:06

from django.db import migrations, models
import fbla.utils


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0014_alter_report_report"),
    ]

    operations = [
        migrations.AlterField(
            model_name="report",
            name="report",
            field=models.FileField(
                blank=True,
                upload_to=fbla.utils.report_upload_path,
                verbose_name="Report File",
            ),
        ),
    ]
