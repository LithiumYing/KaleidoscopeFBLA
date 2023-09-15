# Generated by Django 4.1.4 on 2023-06-21 17:35

from django.db import migrations, models


class Migration(migrations.Migration):
    dependencies = [
        ("fbla", "0019_alter_report_breakdown_alter_report_graphs_and_more"),
    ]

    operations = [
        migrations.AlterField(
            model_name="report",
            name="breakdown",
            field=models.BooleanField(
                default=True, verbose_name="Show in-depth breakdown of student points"
            ),
        ),
        migrations.AlterField(
            model_name="report",
            name="graphs",
            field=models.BooleanField(
                default=True, verbose_name="Show data distributions by grade"
            ),
        ),
        migrations.AlterField(
            model_name="report",
            name="winners",
            field=models.BooleanField(
                default=True, verbose_name="Show winners of prizes"
            ),
        ),
    ]
