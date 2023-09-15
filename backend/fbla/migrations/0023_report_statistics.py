# Generated by Django 4.1.4 on 2023-06-25 22:33

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0022_alter_backup_file"),
    ]

    operations = [
        migrations.AddField(
            model_name="report",
            name="statistics",
            field=models.BooleanField(
                default=True,
                verbose_name="Show statistics of student point data by grade",
            ),
        ),
    ]