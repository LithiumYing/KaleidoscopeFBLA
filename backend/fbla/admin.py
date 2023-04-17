from django.contrib import admin
from .models import (
    Student,
    Event,
    AvailableStudent,
    Prize,
    BonusPoints,
    Report,
    StudentEventRelation,
)
from .forms import AvailableStudentCSVImportForm, StudentCSVImportForm
from .management.commands.import_available_students_by_csv import (
    Command as AvailableStudentCSVImportCommand,
)
from .management.commands.import_students_by_csv import (
    Command as StudentCSVImportCommand,
)
from django.contrib import admin
from django.urls import path
from django.http import HttpResponseRedirect
from io import StringIO
from django.shortcuts import render


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


class ReportAdmin(admin.ModelAdmin):
    exclude = ("year",)


class StudentEventRelationAdmin(admin.ModelAdmin):
    def changelist_view(self, request, extra_context=None):
        extra_context = {"title": "Add Student to Event"}
        return super().changelist_view(request, extra_context=extra_context)


class PrizeAdmin(admin.ModelAdmin):
    actions = ["reset_selected_prizes"]

    def reset_selected_prizes(self, request, queryset):
        queryset.filter(student__isnull=False).update(student=None)

    reset_selected_prizes.short_description = "Reset selected prizes"


admin.site.register(Student, StudentAdmin)
admin.site.register(Event)
admin.site.register(AvailableStudent, AvailableStudentAdmin)
admin.site.register(Prize, PrizeAdmin)
admin.site.register(BonusPoints)
admin.site.register(Report, ReportAdmin)
admin.site.register(StudentEventRelation, StudentEventRelationAdmin)
