# Generated by Django 4.1.4 on 2023-06-25 22:16

from django.db import migrations, models
import fbla.utils


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0021_backup_alter_event_is_recurring_and_more"),
    ]

    operations = [
        migrations.AlterField(
            model_name="backup",
            name="file",
            field=models.FileField(
                upload_to=fbla.utils.backup_upload_path, verbose_name="Backup File"
            ),
        ),
    ]
