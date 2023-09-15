import csv
from django.core.management.base import BaseCommand
from fbla.models import Student
from django.contrib.auth.models import User
import io
import os
import logging


class Command(BaseCommand):
    help = "Import Student data from a CSV file"

    def add_arguments(self, parser):
        parser.add_argument("csvfile", type=str, help="The path to the CSV file")

    def handle(self, *args, **options):
        logger = logging.getLogger(__name__)
        csvfile = options["csvfile"]
        file = None
        if type(csvfile) == str or type(csvfile) == os.PathLike:
            file = open(csvfile, "r")
            csvfile = io.StringIO(file.read())
        elif type(csvfile) == bytes:
            file = open(csvfile, "rb")
            csvfile = io.StringIO(file.read().decode("utf-8"))
        with csvfile as f:
            reader = csv.DictReader(f)
            for row in reader:
                email = row["email"]
                first_name = row["first_name"]
                last_name = row["last_name"]
                password = row["password"]
                user, created = User.objects.get_or_create(
                    email=email,
                    defaults={
                        "username": email,
                        "first_name": first_name,
                        "last_name": last_name,
                    },
                )
                if created:
                    user.set_password(password)
                    user.save()
                elif not user.check_password(password):
                    logger.error(
                        f"User with username {email} exists -- Incorrect password"
                    )
                    continue
                Student.objects.create(
                    student_id=row["student_id"],
                    last_name=last_name,
                    first_name=first_name,
                    graduation_year=row["graduation_year"],
                    user=user,
                )
        if file:
            file.close()
        logger.info(self.style.SUCCESS("Data imported successfully!"))
