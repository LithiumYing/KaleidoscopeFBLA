from django.db import connection
from django.core.management.base import BaseCommand


class Command(BaseCommand):
    help = (
        "Wipes the database of the AvailableStudent, Student, Event, and Prize models"
    )

    def handle(self, *args, **options):
        with connection.cursor() as cursor:
            cursor.execute("TRUNCATE TABLE fbla_student CASCADE;")
            cursor.execute("TRUNCATE TABLE fbla_event CASCADE;")
            cursor.execute("TRUNCATE TABLE fbla_prize CASCADE;")
