from typing import NamedTuple, Dict
from .models import Prize, Report, Student, Event, StudentEventRelation
from .utils import year_grade_bidict
from fpdf import FPDF
from django.core.files.base import ContentFile
from django.core.exceptions import ValidationError
from django.utils.timezone import now
from django.db.models.functions import Random  # type: ignore
from random import choice
import os
import pytz


class GradePrizes(NamedTuple):
    most_points: Prize
    random: Prize


def generate_report() -> str:
    Student.update_points_fields_and_ranks()
    prize_filter = Prize.objects.filter(student__isnull=True)
    if prize_filter.count() < 8:
        raise ValidationError("Not enough prizes to end the quarter!")
    prizes = list(prize_filter.annotate(random_num=Random()).order_by("random_num")[:8])

    grade_prizes_dict: Dict[str, GradePrizes] = {}
    for i, (graduation_year, grade) in enumerate(year_grade_bidict().items()):
        grade_prizes_dict[grade] = GradePrizes(
            most_points=prizes[i * 2], random=prizes[i * 2 + 1]
        )
        grade_prizes_dict[grade].most_points.student = (
            Student.objects.filter(graduation_year=graduation_year)
            .annotate(random_num=Random())
            .order_by("-points", "random_num")[0]
        )
        grade_prizes_dict[grade].most_points.save()
        grade_prizes_dict[grade].random.student = choice(
            Student.objects.filter(
                graduation_year=graduation_year,
                studenteventrelation__in=StudentEventRelation.objects.filter(
                    checked_out=True
                ),
            )
        )
        grade_prizes_dict[grade].random.save()

    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=16)
    pdf.cell(200, 10, txt="End of Quarter Report", ln=1, align="C")
    grade_year_bidict = year_grade_bidict().inverse

    for grade_i, (grade, grade_prizes) in enumerate(grade_prizes_dict.items()):
        graduation_year = grade_year_bidict[grade]
        grade_name = grade + "s" if grade != "Freshman" else grade
        pdf.set_font("Arial", size=14, style="B")
        pdf.cell(
            200, 10, txt=f"{grade_name} -- Class of {graduation_year}", ln=1, align="C"
        )
        pdf.set_font("Arial", size=12)

        pdf.cell(52, 10, txt="Reason", border=1, align="C")
        pdf.cell(69, 10, txt="Winner", border=1, align="C")
        pdf.cell(69, 10, txt="Prize", border=1, align="C")
        pdf.ln()

        pdf.cell(52, 10, txt="Most Points", border=1, align="C")
        winner = grade_prizes.most_points.student
        if not winner:
            raise ValidationError(
                f"Winner not found for most points prize for grade {grade}"
            )
        pdf.cell(
            69,
            10,
            txt=f"{winner.first_name} {winner.last_name} ({winner.student_id})",
            border=1,
            align="C",
        )
        pdf.cell(69, 10, txt=grade_prizes.most_points.name, border=1, align="C")
        pdf.ln()

        pdf.cell(52, 10, txt="Random from Attendees", border=1, align="C")
        winner = grade_prizes.random.student
        if not winner:
            raise ValidationError(
                f"Winner not found for randomly selected prize for grade {grade}"
            )
        pdf.cell(
            69,
            10,
            txt=f"{winner.first_name} {winner.last_name} ({winner.student_id})",
            border=1,
            align="C",
        )
        pdf.cell(69, 10, txt=grade_prizes.random.name, border=1, align="C")
        pdf.ln()

        pdf.ln()
        pdf.set_font("Arial", size=12, style="B")
        pdf.cell(
            200,
            10,
            txt=f"Point Results for {grade + 's' if grade != 'Freshman' else grade}",
            ln=1,
            align="C",
        )
        pdf.set_font("Arial", size=10)

        pdf.cell(17, 10, txt="Rank", border=1, align="C")
        pdf.cell(30, 10, txt="ID Number", border=1, align="C")
        pdf.cell(54, 10, txt="Last Name", border=1, align="C")
        pdf.cell(48, 10, txt="First Name", border=1, align="C")
        pdf.cell(18, 10, txt="Events", border=1, align="C")
        pdf.cell(23, 10, txt="Points", border=1, align="C")
        pdf.ln()

        students = Student.objects.filter(graduation_year=graduation_year).order_by(
            "-points", "last_name", "first_name"
        )
        for i, student in enumerate(students):
            pdf.cell(17, 10, txt=str(i + 1), border=1, align="C")
            pdf.cell(30, 10, txt=str(student.student_id), border=1, align="C")
            pdf.cell(54, 10, txt=student.last_name, border=1, align="C")
            pdf.cell(48, 10, txt=student.first_name, border=1, align="C")
            pdf.cell(
                18,
                10,
                txt=str(
                    student.event_set.filter(elapsed=True, active_points=True).count()  # type: ignore
                ),
                border=1,
                align="C",
            )
            pdf.cell(23, 10, txt=str(student.points), border=1, align="C")
            pdf.ln()

        if grade_i != len(grade_prizes_dict) - 1:
            pdf.add_page()
    pdf_file_path = "output.pdf"
    pdf.output(pdf_file_path)
    with open(pdf_file_path, "rb") as f:
        pdf_contents = f.read()  # this is kind of funky but the library requires it
    os.remove(pdf_file_path)
    report = Report.objects.create(
        report=ContentFile(
            content=pdf_contents,
            name=f"Report--{now().astimezone(pytz.timezone('America/Chicago')).strftime('%b-%d-%Y--%I-%M-%S-%p')}.pdf",
        ),
    )
    # uncomment for non-test enviornment
    # Event.objects.all().filter(active_points=True).update(active_points=False)
    Student.update_points_fields_and_ranks()
    return report.report.url
