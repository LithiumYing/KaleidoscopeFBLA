from typing import NamedTuple, Dict, Optional
from .models import Prize, Report, Student, Event
from .utils import year_grade_bidict
from fpdf import FPDF, Align
from django.core.files.base import ContentFile
from django.core.exceptions import ValidationError
from django.utils.timezone import now
from django.db.models.functions import Random
from random import choice
import pytz
from matplotlib.figure import Figure
import io
from PIL import Image
from statistics import median, mean, stdev


class GradePrizes(NamedTuple):
    most_points: Prize
    random: Prize


def add_page_if_needed(pdf: FPDF) -> None:
    if pdf.get_y() > 0.65 * pdf.h:
        pdf.add_page()


def points_list() -> list[list[int]]:
    grade_year_bidict = year_grade_bidict().inverse
    points_by_year = {year: [] for year in grade_year_bidict.values()}
    for student in Student.objects.all():
        target_points_list = points_by_year.get(student.graduation_year, None)
        if target_points_list is not None:
            target_points_list.append(student.points)
    return list(points_by_year.values())


def generate_report(
    *,
    winners=True,
    breakdown=True,
    graphs=True,
    statistics=True,
    percent_of_students_at_or_below_event_number: Optional[int] = None,
) -> str:
    """This function generates a report for the end of the quarter.

    Args:
        winners (bool, optional): Determines if the generated report includes a list of prize winners. Defaults to True.
        breakdown (bool, optional): Determines if the generated report incldes an in-depth breakdown of students. Defaults to True.
        graphs (bool, optional): Determines if the generated report includes distribution visualizations. Defaults to True.

    Raises:
        ValidationError: Less than 8 prizes
        ValidationError: No students in a grade exist
        ValidationError: No students in a grade attended an event

    Returns:
        str: URL of the generated report
    """
    if percent_of_students_at_or_below_event_number is not None:
        statistics = True
    Student.update_points_fields_and_ranks()
    prize_filter = Prize.objects.filter(student__isnull=True)
    if prize_filter.count() < 8:
        raise ValidationError("Not enough prizes to end the quarter!")
    prizes = list(prize_filter.annotate(random_num=Random()).order_by("random_num")[:8])

    grade_prizes_dict: Dict[str, GradePrizes] = {}
    year_grade_bidict_instance = year_grade_bidict()
    grade_year_bidict = year_grade_bidict_instance.inverse
    for i, (graduation_year, grade) in enumerate(year_grade_bidict_instance.items()):
        grade_prizes_dict[grade] = GradePrizes(
            most_points=prizes[i * 2], random=prizes[i * 2 + 1]
        )
        student_query_set = (
            Student.objects.filter(graduation_year=graduation_year)
            .annotate(random_num=Random())
            .order_by("-points", "random_num")
        )
        grade_prizes_dict[grade].most_points.student = student_query_set[0]
        student_query_set = student_query_set.exclude(
            pk=grade_prizes_dict[grade].most_points.student.pk
        )
        grade_prizes_dict[grade].most_points.save()
        grade_prizes_dict[grade].random.student = choice(
            student_query_set.filter(studenteventrelation__checked_out=True)
        )
        grade_prizes_dict[grade].random.save()

    pdf = FPDF()
    pdf.add_page()
    pdf.set_font("Arial", size=18, style="B")
    pdf.cell(200, 10, txt="End of Quarter Report", ln=1, align="C")
    list_of_points = points_list()
    if graphs:
        pdf.set_font("Arial", size=15, style="B")
        pdf.ln(5)
        pdf.cell(
            200,
            10,
            txt=f"Distribution of Hours Spent at Events by Grade Level",
            ln=1,
            align="C",
        )
        hours_list = [[points // 500 for points in grade] for grade in list_of_points]
        fig = Figure()
        axs = fig.subplots(2, 2)
        axs[0, 0].violinplot(
            hours_list[0],
            showmeans=True,
            showmedians=False,
            showextrema=False,
            vert=True,
        )
        axs[0, 0].set_title("Freshmen")
        axs[0, 0].set_ylabel("Hours Attended")
        axs[0, 1].violinplot(
            hours_list[1],
            showmeans=True,
            showmedians=False,
            showextrema=False,
            vert=True,
        )
        axs[0, 1].set_title("Sophomores")
        axs[1, 0].violinplot(
            hours_list[2],
            showmeans=True,
            showmedians=False,
            showextrema=False,
            vert=True,
        )
        axs[1, 0].set_title("Juniors")
        axs[1, 0].set_ylabel("Hours Attended")
        axs[1, 1].violinplot(
            hours_list[3],
            showmeans=True,
            showmedians=False,
            showextrema=False,
            vert=True,
        )
        axs[1, 1].set_title("Seniors")
        for row in axs:
            for ax in row:
                ax.set_xticks([])
        img_buf = io.BytesIO()
        fig.savefig(img_buf, format="png")
        img = Image.open(img_buf)
        width, height = img.size
        img = img.crop((0, 20, width, height - 20))
        pdf.image(
            img,
            Align.C,
            w=int(pdf.epw / 1.3),
            alt_text="Distributions of points by student grade level",
        )
        pdf.set_font("Arial", size=12)
        pdf.cell(
            200,
            10,
            txt="Conversion ratio of points to hours: 500 points = 1 hour",
            ln=1,
            align="C",
        )
    if statistics:
        if graphs:
            pdf.ln(5)
        add_page_if_needed(pdf)
        pdf.set_font("Arial", size=15, style="B")
        pdf.cell(
            200,
            10,
            txt=f"Statistics",
            ln=1,
            align="C",
        )
        pdf.set_font("Arial", size=12, style="B")
        if percent_of_students_at_or_below_event_number is None:
            n1, n2, n3, n4 = 4, 9, 9, 12
        else:
            n1, n2, n3, n4 = 0, 0, 0, 0
        pdf.cell(30 + n1, 10, txt="Grade", border=1, align="C")
        pdf.set_font("Arial", size=12)
        pdf.cell(34 + n2, 10, txt="Average", border=1, align="C")
        pdf.cell(34 + n3, 10, txt="Median", border=1, align="C")
        pdf.cell(58 + n4, 10, txt="Standard Deviation", border=1, align="C")
        if percent_of_students_at_or_below_event_number is not None:
            pdf.cell(
                34,
                10,
                txt=f"% < {percent_of_students_at_or_below_event_number} Events",
                border=1,
                align="C",
            )
        pdf.ln(10)
        for grade, points in zip(year_grade_bidict_instance.values(), list_of_points):
            pdf.cell(30 + n1, 10, txt=grade, border=1, align="C")
            pdf.cell(34 + n2, 10, txt=str(round(mean(points), 2)), border=1, align="C")
            pdf.cell(
                34 + n3, 10, txt=str(round(median(points), 2)), border=1, align="C"
            )
            pdf.cell(58 + n4, 10, txt=str(round(stdev(points), 2)), border=1, align="C")
            if percent_of_students_at_or_below_event_number is not None:
                total = 0
                under_threshold = 0
                for s in Student.objects.filter(
                    graduation_year=grade_year_bidict[grade]
                ):
                    if (
                        s.event_set.count()
                        < percent_of_students_at_or_below_event_number
                    ):
                        under_threshold += 1
                    total += 1
                pdf.cell(
                    34,
                    10,
                    txt=f"{round(under_threshold/total*100, 2)}%",
                    border=1,
                    align="C",
                )
            pdf.ln(10)
    for grade_i, (grade, grade_prizes) in enumerate(grade_prizes_dict.items()):
        if statistics:
            pdf.ln(5)
        graduation_year = grade_year_bidict[grade]
        if winners or breakdown:
            add_page_if_needed(pdf)
            grade_name = grade + "s" if grade != "Freshman" else "Freshmen"
            pdf.set_font("Arial", size=15, style="B")
            pdf.cell(
                200,
                10,
                txt=f"{grade_name} -- Class of {graduation_year}",
                ln=1,
                align="C",
            )
        if winners:
            pdf.set_font("Arial", size=13, style="B")
            pdf.cell(
                200,
                10,
                txt=f"Prizes",
                ln=1,
                align="C",
            )
            pdf.set_font("Arial", size=12)

            pdf.cell(52, 10, txt="Reason", border=1, align="C")
            pdf.cell(69, 10, txt="Winner", border=1, align="C")
            pdf.cell(69, 10, txt="Prize", border=1, align="C")
            pdf.ln(10)

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
            pdf.ln(10)

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
            pdf.ln(10)
        if breakdown:
            if winners:
                pdf.ln(5)
            add_page_if_needed(pdf)
            pdf.set_font("Arial", size=13, style="B")
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
            pdf.ln(10)

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
                        student.event_set.filter(
                            elapsed=True, active_points=True
                        ).count()
                    ),
                    border=1,
                    align="C",
                )
                pdf.cell(23, 10, txt=str(student.points), border=1, align="C")
                pdf.ln(10)

        if grade_i != len(grade_prizes_dict) - 1:
            pdf.ln(5)
    pdf_contents = bytes(pdf.output())
    report = Report.objects.create(
        report=ContentFile(
            content=pdf_contents,
            name=f"Report--{now().astimezone(pytz.timezone('America/New_York')).strftime('%b-%d-%Y--%I-%M-%S-%p')}.pdf",
        ),
    )
    # uncomment for non-test enviornment
    # Event.objects.all().filter(active_points=True).update(active_points=False)
    Student.update_points_fields_and_ranks()
    return report.report.url
