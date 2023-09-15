import logging

from django.conf import settings

from django.utils.timezone import now
from apscheduler.schedulers.blocking import BlockingScheduler
from apscheduler.triggers.cron import CronTrigger
from django.core.management.base import BaseCommand
from fbla.models import Event, Student
from fbla.admin import BackupAdmin
from typing import Callable
from datetime import timedelta

logger = logging.getLogger(__name__)
log_job: Callable[[str], None] = lambda id: logger.info(f"Added job {id}")


def end_events():
    for event in Event.objects.filter(
        end_time__lte=now() + timedelta(minutes=15),
        elapsed=False,
        always_checkin_out_able=False,
    ):
        try:
            if not event.end_event():
                raise Exception(
                    "Something's wrong in filter, this event already ended!"
                )
        except Exception as e:
            logger.error(f"Error ending event {event}: {e}")


class Command(BaseCommand):
    help = "Runs APScheduler."

    def handle(self, *args, **options):
        scheduler = BlockingScheduler(timezone=settings.TIME_ZONE)
        id = "end_events"
        scheduler.add_job(
            end_events,
            trigger=CronTrigger(second="*/10"),  # Every 10 seconds
            id=id,
            max_instances=1,
            replace_existing=True,
        )
        log_job(id)
        id = "update_points_fields_and_ranks"
        scheduler.add_job(
            Student.update_points_fields_and_ranks,
            trigger=CronTrigger(hour="*/1"),  # Every hour
            id=id,
            max_instances=1,
            replace_existing=True,
        )
        log_job(id)
        id = "backup"
        scheduler.add_job(
            BackupAdmin.create_backup_internal,
            trigger=CronTrigger(hour="*/24"),  # Every day
            id=id,
            max_instances=1,
            replace_existing=True,
        )
        log_job(id)
        try:
            logger.info("Starting scheduler...")
            scheduler.start()
        except KeyboardInterrupt:
            logger.info("Stopping scheduler...")
            scheduler.shutdown()
            logger.info("Scheduler shut down successfully!")
