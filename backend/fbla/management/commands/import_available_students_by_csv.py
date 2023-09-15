import csv
from django.core.management.base import BaseCommand
from fbla.models import AvailableStudent
import io
import os
import logging


class Command(BaseCommand):
    help = "Import AvailableStudent data from a CSV file"

    def add_arguments(self, parser):
        parser.add_argument("csvfile", type=str, help="The path to the CSV file")

    def handle(self, *args, **options):
        csvfile = options["csvfile"]
        file = None
        logger = logging.getLogger(__name__)
        if type(csvfile) == str or type(csvfile) == os.PathLike:
            file = open(csvfile, "r")
            csvfile = io.StringIO(file.read())
        elif type(csvfile) == bytes:
            file = open(csvfile, "rb")
            csvfile = io.StringIO(file.read().decode("utf-8"))
        with csvfile as f:
            reader = csv.DictReader(f)
            for row in reader:
                AvailableStudent.objects.create(
                    student_id=row["student_id"],
                    last_name=row["last_name"],
                    first_name=row["first_name"],
                    graduation_year=row["graduation_year"],
                )
        if file:
            file.close()
        logger.info(self.style.SUCCESS("Data imported successfully!"))
