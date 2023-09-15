from django.utils.timezone import now
from django.conf import settings
from django.db.models import ImageField
from bidict import bidict
from uuid import uuid4


def get_school_year() -> int:
    time = now()
    year = time.year
    if time.month >= 7:
        year += 1
    return year


def year_grade_bidict() -> bidict[int, str]:
    year = get_school_year()
    return bidict(
        {
            year + 3: "Freshman",
            year + 2: "Sophomore",
            year + 1: "Junior",
            year: "Senior",
        }
    )


def url_from_image_field(image: ImageField) -> str:
    prefix = f"{settings.AWS_STORAGE_BUCKET_NAME}.{settings.AWS_S3_REGION_NAME}"
    return image.url.replace(prefix, f"{prefix}.cdn")


def image_upload_path(_, filename: str) -> str:
    return f"uploads/{uuid4()}.{filename.split('.')[-1]}"


def report_upload_path(_, filename: str) -> str:
    return f"reports/{filename}"


def backup_upload_path(_, filename: str) -> str:
    return f"backups/{filename}"
