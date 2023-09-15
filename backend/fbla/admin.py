from typing import Any, Optional
from django.contrib import admin
from django.http.request import HttpRequest
from django.http.response import HttpResponseRedirectBase, HttpResponse
from .models import (
    Student,
    Event,
    AvailableStudent,
    Prize,
    BonusPoints,
    Report,
    StudentEventRelation,
    Backup,
)
from .forms import AvailableStudentCSVImportForm, StudentCSVImportForm
from .management.commands.import_available_students_by_csv import (
    Command as AvailableStudentCSVImportCommand,
)
from .management.commands.import_students_by_csv import (
    Command as StudentCSVImportCommand,
)
from django.contrib import admin, messages
from django.urls import path
from django.http import HttpResponseRedirect
from io import StringIO
from django.shortcuts import render, redirect
from django.contrib import admin
import tempfile
import pytz
from django.utils.timezone import now
from django.core import management
import os
from django.core.files.base import ContentFile
from django.db.models import QuerySet
from django.shortcuts import reverse


class BackupAdmin(admin.ModelAdmin):
    list_display = ("file",)
    readonly_fields = ("file",)

    actions = ["load_backup"]

    @staticmethod
    def create_backup_internal() -> None:
        filename = f"Backup--{now().astimezone(pytz.timezone('America/New_York')).strftime('%b-%d-%Y--%I-%M-%S-%p')}.backup"
        temp_dir = tempfile.mkdtemp()
        local_path = os.path.join(temp_dir, filename)
        management.call_command(
            "dbbackup",
            "--noinput",
            "--output-path",
            local_path,
            "--quiet",
        )
        with open(local_path, "rb") as f:
            Backup.objects.create(file=ContentFile(content=f.read(), name=filename))
        os.remove(local_path)
        os.rmdir(temp_dir)

    def create_backup(
        self, request, queryset: Optional[QuerySet[Backup]] = None
    ) -> None:
        BackupAdmin.create_backup_internal()
        if self is not None and request is not None:
            self.message_user(request, "Backup created successfully")

    def load_backup(self, request, queryset: QuerySet[Backup]):
        if queryset.count() != 1:
            self.message_user(
                request, "Please select only one backup to load.", level=messages.ERROR
            )
            return
        backup = queryset.first()
        old_filename = backup.file.name.replace("backups/", "")

        with tempfile.NamedTemporaryFile() as f:
            with backup.file.open() as backup_file:
                backup_bytes = backup_file.read()
                f.write(backup_bytes)
            management.call_command(
                "dbrestore",
                "--noinput",
                "--input-path",
                f.name,
                "--quiet",
            )

        Backup.objects.create(file=ContentFile(content=backup_bytes, name=old_filename))
        self.message_user(request, "Backup loaded successfully")

    load_backup.short_description = "Load selected backup"

    def add_view(
        self, request: HttpRequest, form_url: str = ..., extra_context: None = ...
    ) -> HttpResponse:
        BackupAdmin.create_backup_internal()
        return HttpResponseRedirect(reverse("admin:fbla_backup_changelist"))


class AvailableStudentAdmin(admin.ModelAdmin):
    change_list_template = "admin/fbla/available_student_change_list_with_import.html"

    def import_data(self, request):
        if request.method == "GET":
            form = AvailableStudentCSVImportForm()
            context = {
                "app_label": self.model._meta.app_label,
                "model_name": self.model._meta.model_name,
                "form": form,
                "opts": self.model._meta,
                "app_config": self.admin_site.name,
            }
            return render(
                request, "admin/fbla/available_student_import_data.html", context
            )

        elif request.method == "POST":
            form = AvailableStudentCSVImportForm(request.POST, request.FILES)
            if form.is_valid():
                csvfile = request.FILES["csvfile"]
                content = csvfile.read().decode("utf-8")
                cmd = AvailableStudentCSVImportCommand()
                cmd.handle(csvfile=StringIO(content))
                self.message_user(request, "Data imported successfully!")
                return HttpResponseRedirect(request.get_full_path())

        else:
            form = AvailableStudentCSVImportForm()

        context = {
            "app_label": self.model._meta.app_label,
            "model_name": self.model._meta.model_name,
            "form": form,
            "opts": self.model._meta,
            "app_config": self.admin_site.name,
        }
        return render(request, "admin/fbla/available_student_import_data.html", context)

    import_data.short_description = "Import available student data from CSV"

    def get_urls(self):
        urls = super().get_urls()
        custom_urls = [
            path(
                "import_data/",
                self.admin_site.admin_view(self.import_data),
                name="fbla_availablestudent_import_data",
            ),
        ]
        return custom_urls + urls


class StudentAdmin(admin.ModelAdmin):
    actions = ["import_data"]
    change_list_template = "admin/fbla/student_change_list_with_import.html"

    def import_data(self, request):
        if request.method == "GET":
            form = StudentCSVImportForm()
            context = {
                "app_label": self.model._meta.app_label,
                "model_name": self.model._meta.model_name,
                "form": form,
            }
            return render(request, "admin/fbla/student_import_data.html", context)

        elif request.method == "POST":
            form = StudentCSVImportForm(request.POST, request.FILES)
            if form.is_valid():
                csvfile = request.FILES["csvfile"]
                content = csvfile.read().decode("utf-8")
                cmd = StudentCSVImportCommand()
                cmd.handle(csvfile=StringIO(content))
                self.message_user(request, "Data imported successfully!")
                return HttpResponseRedirect(request.get_full_path())

        else:
            form = StudentCSVImportForm()

        context = {
            "app_label": self.model._meta.app_label,
            "model_name": self.model._meta.model_name,
            "form": form,
            "opts": self.model._meta,
            "app_config": self.admin_site.name,
        }
        return render(request, "admin/fbla/student_import_data.html", context)

    import_data.short_description = "Import student data from CSV"
    
    def get_urls(self):
        urls = super().get_urls()
        custom_urls = [
            path(
                "import_data/",
                self.admin_site.admin_view(self.import_data),
                name="fbla_student_import_data",
            ),
        ]
        return custom_urls + urls

    def get_form(
        self, request: Any, obj: Any | None = ..., change: bool = ..., **kwargs: Any
    ) -> Any:
        if obj is None:
            self.exclude = ("points", "rank")
            self.readonly_fields = tuple()
        else:
            self.exclude = tuple()
            self.readonly_fields = ("points", "rank")
        return super().get_form(request, obj, change, **kwargs)


class ReportAdmin(admin.ModelAdmin):
    list_display = ("report",)

    def response_post_save_add(
        self, request: HttpRequest, obj
    ) -> HttpResponseRedirectBase:
        return redirect(Report.objects.latest("created_at").report.url)

    def get_form(
        self, request: Any, obj: Any | None = ..., change: bool = ..., **kwargs: Any
    ) -> Any:
        if obj is None:
            self.exclude = (
                "year",
                "report",
            )
            self.readonly_fields = tuple()
        else:
            self.exclude = ("year", "breakdown", "graphs", "winners", "statistics")
            self.readonly_fields = ("report",)
        return super().get_form(request, obj, change, **kwargs)


class StudentEventRelationAdmin(admin.ModelAdmin):
    def changelist_view(self, request, extra_context=None):
        extra_context = {"title": "Add Student to Event"}
        return super().changelist_view(request, extra_context=extra_context)


class PrizeAdmin(admin.ModelAdmin):
    actions = ["reset_selected_prizes"]

    def reset_selected_prizes(self, request, queryset):
        queryset.filter(student__isnull=False).update(student=None)

    reset_selected_prizes.short_description = "Reset selected prizes"


class EventAdmin(admin.ModelAdmin):
    exclude = (
        "elapsed",
        "active_points",
        "past_count",
        "always_checkin_out_able",
    )


admin.site.register(Student, StudentAdmin)
admin.site.register(Event, EventAdmin)
admin.site.register(AvailableStudent, AvailableStudentAdmin)
admin.site.register(Prize, PrizeAdmin)
admin.site.register(BonusPoints)
admin.site.register(Report, ReportAdmin)
admin.site.register(StudentEventRelation, StudentEventRelationAdmin)
admin.site.register(Backup, BackupAdmin)
