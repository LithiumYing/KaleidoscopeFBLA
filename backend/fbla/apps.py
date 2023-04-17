from django.apps import AppConfig


class FblaConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "fbla"
    verbose_name = "Event Tracking"

    def ready(self):
        from django.apps import apps
        from django.contrib import admin
        from django.contrib.auth.models import Group
        from admin_interface.models import Theme
        from django_apscheduler.models import DjangoJob, DjangoJobExecution

        apps.get_app_config("auth").verbose_name = "Student/Staff Account Management"

        admin.site.unregister(Theme)
        admin.site.unregister(DjangoJob)
        admin.site.unregister(DjangoJobExecution)
        admin.site.unregister(Group)
        from .forms import CustomUserCreationForm
        from django.contrib.auth.models import User
        from django.contrib.auth.admin import UserAdmin

        class CustomUserAdmin(UserAdmin):
            add_form = CustomUserCreationForm
            add_fieldsets = (
                (
                    None,
                    {
                        "classes": ("wide",),
                        "fields": (
                            "first_name",
                            "last_name",
                            "email",
                            "password1",
                            "password2",
                            "is_staff",
                            "student_id",
                        ),
                    },
                ),
            )

        admin.site.unregister(User)
        admin.site.register(User, CustomUserAdmin)
