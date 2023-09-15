from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from .models import Student


class AvailableStudentCSVImportForm(forms.Form):
    csvfile = forms.FileField(
        label="CSV file of available students",
        help_text="Format: student_id, last_name, first_name, graduation_year",
    )


class StudentCSVImportForm(forms.Form):
    csvfile = forms.FileField(
        label="CSV file of students",
        help_text="Format: student_id, last_name, first_name, graduation_year, email, password",
    )


class CustomUserCreationForm(UserCreationForm):
    is_staff = forms.BooleanField(label="Staff Member", required=False, initial=True)
    first_name = forms.CharField(label="First Name", max_length=32, required=True)
    last_name = forms.CharField(label="Last Name", max_length=32, required=True)
    email = forms.EmailField(label="Email", max_length=254, required=True)
    student_id = forms.IntegerField(
        label="ID",
        required=False,
        help_text="Leave blank to not create corresponding student account.",
    )

    class Meta:
        model = User
        fields = (
            "first_name",
            "last_name",
            "email",
            "password1",
            "password2",
            "is_staff",
            "student_id",
        )

    def save(self, commit=True):
        self.username = self.cleaned_data["email"]
        user = super().save(commit=False)
        user.email = self.cleaned_data["email"]
        user.username = self.username
        user.is_superuser = self.cleaned_data["is_staff"]
        user.last_name = self.cleaned_data.get("last_name")
        user.first_name = self.cleaned_data.get("first_name")
        student_id = self.cleaned_data.get("student_id")
        if student_id or commit:
            user.save()
        if student_id:
            student = Student.objects.create(
                student_id=student_id,
                last_name=user.last_name,
                first_name=user.first_name,
                user=user,
            )
            student.save()
        return user
