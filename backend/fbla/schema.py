import graphene
from .models import (
    AvailableStudent,
    Student,
    Event,
    Prize,
    BonusPoints,
    StudentEventRelation,
)
from .query import (
    EventType,
    AvailableStudentType,
    BonusPointsType,
    StudentType,
    PrizeType,
    Query,
)
from .report import generate_report
import graphql_jwt
from graphql_jwt import ObtainJSONWebToken
from graphql_jwt.shortcuts import get_token
from graphql_jwt.decorators import login_required, staff_member_required
from graphql_jwt.exceptions import PermissionDenied
from graphene_file_upload.scalars import Upload
from django.core.files.images import ImageFile
from django.contrib.auth.models import User


def require_staff_or_self(info, student: Student) -> None:
    if not info.context.user.is_staff and student.user != info.context.user:
        raise PermissionDenied("You do not have permission to perform this action")


class VerifyTokenWithJWT(graphql_jwt.Verify):
    @classmethod
    def mutate(cls, root, info, **kwargs):
        kwargs["token"] = kwargs["token"][4:]
        return super().mutate(root, info, **kwargs)


class CreateTokenWithJWT(ObtainJSONWebToken):
    @classmethod
    def mutate(cls, root, info, **kwargs):
        result = super().mutate(root, info, **kwargs)
        try:
            result.token = f"JWT {result.token}"
        except (AttributeError, KeyError):
            pass
        return result


class CreateAvailableStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)
        last_name = graphene.String(required=True)
        first_name = graphene.String(required=True)
        graduation_year = graphene.Int(required=True)

    available_student = graphene.Field(AvailableStudentType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, student_id, last_name, first_name, graduation_year):
        return CreateAvailableStudentMutation(
            available_student=AvailableStudent.objects.create(
                student_id=student_id,
                last_name=last_name,
                first_name=first_name,
                graduation_year=graduation_year,
            )
        )


class DeleteAvailableStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)

    available_student = graphene.Field(AvailableStudentType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, student_id):
        available_student = AvailableStudent.objects.get(student_id=student_id)
        available_student.delete()
        return DeleteAvailableStudentMutation(available_student=available_student)


class CreateStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)
        email = graphene.String(required=True)
        password = graphene.String(required=True)

    student = graphene.Field(StudentType, required=True)
    token = graphene.String(required=True)

    @classmethod
    def mutate(cls, root, info, student_id, email, password):
        try:
            availablestudent = AvailableStudent.objects.get(student_id=student_id)
        except AvailableStudent.DoesNotExist:
            raise Exception("Student not available")
        first_name = availablestudent.first_name
        last_name = availablestudent.last_name
        graduation_year = availablestudent.graduation_year
        user, created = User.objects.get_or_create(
            email=email,
            defaults={
                "username": email,
                "first_name": first_name,
                "last_name": last_name,
            },
        )
        if created:
            user.set_password(password)
            user.save()
        elif not user.check_password(password):
            raise Exception("Student exists -- Incorrect password")
        student = Student.objects.create(
            student_id=student_id,
            last_name=last_name,
            first_name=first_name,
            graduation_year=graduation_year,
            user=user,
        )
        availablestudent.delete()
        Student.update_points_fields_and_ranks()
        return CreateStudentMutation(
            student=student, token=f"JWT {get_token(user, info.context)}"
        )


class CreateEventMutation(graphene.Mutation):
    class Arguments:
        name = graphene.String(required=True)
        location = graphene.String(required=True)
        start_time = graphene.DateTime(required=True)
        end_time = graphene.DateTime(required=True)
        is_recurring = graphene.Boolean(required=True)
        image = Upload(required=False)

    event = graphene.Field(EventType, required=True)

    @classmethod
    @staff_member_required
    def mutate(
        cls, root, info, name, location, start_time, end_time, is_recurring, image=None
    ):
        event = Event.objects.create(
            name=name,
            location=location,
            start_time=start_time,
            end_time=end_time,
            is_recurring=is_recurring,
            image_field=image,
        )
        return CreateEventMutation(event=event)


class CreatePrizeMutation(graphene.Mutation):
    class Arguments:
        name = graphene.String(required=True)
        description = graphene.String(required=True)
        image = Upload(required=False)
        student_id = graphene.Int(required=False)

    prize = graphene.Field(PrizeType, required=True)

    def mutate(self, info, name, description, image=None, student_id=None):
        prize = Prize(
            name=name,
            description=description,
            student=Student.objects.get(student_id=student_id) if student_id else None,
        )

        if image:
            prize.image_field.save(image.name, ImageFile(image, name=image.name))
            prize.save()

        return CreatePrizeMutation(prize=prize)


class UpdatePrizeMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)
        name = graphene.String(required=False)
        description = graphene.String(required=False)
        image = Upload(required=False)
        student_id = graphene.Int(required=False)

    prize = graphene.Field(PrizeType, required=True)

    def mutate(
        self, info, id, name=None, description=None, image=None, student_id=None
    ):
        prize = Prize.objects.get(id=id)
        if name is not None:
            prize.name = name
        if description is not None:
            prize.description = description
        if image is not None:
            prize.image_field.save(image.name, ImageFile(image, name=image.name))
        if student_id is not None:
            prize.student = Student.objects.get(
                student=Student.objects.get(student_id=student_id)
            )
        prize.save()
        return UpdatePrizeMutation(prize=prize)


class DeletePrizeMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)

    prize = graphene.Field(PrizeType, required=True)

    def mutate(self, info, id):
        prize = Prize.objects.get(id=id)
        prize.delete()
        return DeletePrizeMutation(prize=prize)


class UpdateEventMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)
        name = graphene.String(required=False)
        location = graphene.String(required=False)
        start_time = graphene.DateTime(required=False)
        end_time = graphene.DateTime(required=False)
        is_recurring = graphene.Boolean(required=False)
        total_count = graphene.Int(required=False)
        past_count = graphene.Int(required=False)
        image = Upload(required=False)

    event = graphene.Field(EventType, required=True)

    @classmethod
    @staff_member_required
    def mutate(
        cls,
        root,
        info,
        id,
        name=None,
        location=None,
        start_time=None,
        end_time=None,
        is_recurring=None,
        total_count=None,
        past_count=None,
        image=None,
    ):
        event = Event.objects.get(id=id)
        if name is not None:
            event.name = name
        if location is not None:
            event.location = location
        if start_time is not None:
            event.start_time = start_time
        if end_time is not None:
            event.end_time = end_time
        if is_recurring is not None:
            event.is_recurring = is_recurring
        if total_count is not None:
            event.total_count = total_count
        if past_count is not None:
            event.past_count = past_count
        if image is not None:
            event.image_field.save(image.name, ImageFile(image, name=image.name))
        event.save()
        return UpdateEventMutation(event=event)


class UpdateStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)
        last_name = graphene.String(required=False)
        first_name = graphene.String(required=False)
        graduation_year = graphene.Int(required=False)

    student = graphene.Field(StudentType, required=True)

    @classmethod
    @login_required
    def mutate(
        cls,
        root,
        info,
        student_id,
        last_name=None,
        first_name=None,
        graduation_year=None,
    ):
        student = Student.objects.get(student_id=student_id)
        require_staff_or_self(info, student)
        student.student_id = student_id
        if last_name is not None:
            student.last_name = last_name
        if first_name is not None:
            student.first_name = first_name
        if graduation_year is not None:
            student.graduation_year = graduation_year
        student.save()
        return UpdateStudentMutation(student=student)


class AddStaffToStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)

    student = graphene.Field(StudentType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, student_id):
        student = Student.objects.get(student_id=student_id)
        student.user.is_staff = True
        student.save()
        return AddStaffToStudentMutation(student=student)


class CheckInOrOutStudentFromEvent(graphene.Mutation):
    class Arguments:
        event_id = graphene.ID(required=True)
        student_id = graphene.Int(required=True)

    event = graphene.Field(EventType, required=True)
    student = graphene.Field(StudentType, required=True)

    @classmethod
    @login_required
    def mutate(cls, root, info, event_id, student_id):
        student = Student.objects.get(student_id=student_id)
        require_staff_or_self(info, student)
        event = Event.objects.get(id=event_id)
        if not event.attendees.filter(student_id=student_id).exists():
            if not event.is_checkin_open():
                raise Exception("Check-in is not open yet. Come back later!")
            else:
                event.attendees.add(student)
        else:
            relation = StudentEventRelation.objects.get(student=student, event=event)
            if not relation.checked_out:
                if not event.is_checkout_open():
                    raise Exception("Check-out is not open yet. Come back later!")
                else:
                    relation.checked_out = True
                    relation.save(update_fields=["checked_out"])
        event.save()
        return CheckInOrOutStudentFromEvent(event=event, student=student)


class RemoveStudentFromEventMutation(graphene.Mutation):
    class Arguments:
        event_id = graphene.ID(required=True)
        student_id = graphene.Int(required=True)

    event = graphene.Field(EventType, required=True)
    student = graphene.Field(StudentType, required=True)

    @classmethod
    @login_required
    def mutate(cls, root, info, event_id, student_id):
        student = Student.objects.get(student_id=student_id)
        require_staff_or_self(info, student)
        event = Event.objects.get(id=event_id)
        event.attendees.remove(student)
        event.save()
        return RemoveStudentFromEventMutation(event=event, student=student)


class DeleteStudentMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)

    student = graphene.Field(StudentType, required=True)

    @classmethod
    @login_required
    def mutate(cls, root, info, student_id):
        student = Student.objects.get(student_id=student_id)
        require_staff_or_self(info, student)
        student.delete()
        return DeleteStudentMutation(student=student)


class DeleteEventMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)

    event = graphene.Field(EventType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, id):
        event = Event.objects.get(id=id)
        event.delete()
        return DeleteEventMutation(event=event)


class ForceUpdateEventPointsMutation(graphene.Mutation):
    success = graphene.Boolean(required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info):
        for event in Event.objects.all():
            event.save()
        return ForceUpdateEventPointsMutation(success=True)


class EndEventEarlyMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)

    event = graphene.Field(EventType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, id):
        event = Event.objects.get(id=id).end_event()
        return EndEventEarlyMutation(event=event)


class CreateBonusPointsMutation(graphene.Mutation):
    class Arguments:
        student_id = graphene.Int(required=True)
        points = graphene.Int(required=True)
        description = graphene.String(required=True)

    bonus_points = graphene.Field(BonusPointsType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, student_id, points, description):
        bonus_points = BonusPoints.objects.create(
            student=Student.objects.get(student_id=student_id),
            points=points,
            description=description,
        )
        return CreateBonusPointsMutation(bonus_points=bonus_points)


class DeleteBonusPointsMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)

    bonus_points = graphene.Field(BonusPointsType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, id):
        bonus_points = BonusPoints.objects.get(id=id)
        bonus_points.delete()
        return DeleteBonusPointsMutation(bonus_points=bonus_points)


class UpdateBonusPointsMutation(graphene.Mutation):
    class Arguments:
        id = graphene.ID(required=True)
        student_id = graphene.Int(required=False)
        points = graphene.Int(required=False)
        reason = graphene.String(required=False)

    bonus_points = graphene.Field(BonusPointsType, required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info, id, student_id=None, points=None, reason=None):
        bonus_points = BonusPoints.objects.get(id=id)
        if points is not None:
            bonus_points.points = points
        if reason is not None:
            bonus_points.reason = reason
        if student_id is not None:
            bonus_points.student = Student.objects.get(student_id=student_id)
        bonus_points.save()
        return UpdateBonusPointsMutation(bonus_points=bonus_points)


class EndQuarterMutation(graphene.Mutation):
    report_url = graphene.String(required=True)

    @classmethod
    @staff_member_required
    def mutate(cls, root, info):
        return EndQuarterMutation(report_url=generate_report())


class Mutation(graphene.ObjectType):
    token_auth = CreateTokenWithJWT.Field()
    verify_token = VerifyTokenWithJWT.Field()

    create_event = CreateEventMutation.Field()
    create_available_student = CreateAvailableStudentMutation.Field()
    create_student = CreateStudentMutation.Field()
    update_event = UpdateEventMutation.Field()
    force_update_event_points = ForceUpdateEventPointsMutation.Field()
    update_student = UpdateStudentMutation.Field()
    check_in_or_out_student_from_event = CheckInOrOutStudentFromEvent.Field()
    remove_student_from_event = RemoveStudentFromEventMutation.Field()
    delete_available_student = DeleteAvailableStudentMutation.Field()
    delete_student = DeleteStudentMutation.Field()
    delete_event = DeleteEventMutation.Field()
    create_prize = CreatePrizeMutation.Field()
    update_prize = UpdatePrizeMutation.Field()
    delete_prize = DeletePrizeMutation.Field()
    end_event_early = EndEventEarlyMutation.Field()
    add_staff_to_student = AddStaffToStudentMutation.Field()
    create_bonus_points = CreateBonusPointsMutation.Field()
    update_bonus_points = UpdateBonusPointsMutation.Field()
    delete_bonus_points = DeleteBonusPointsMutation.Field()
    end_quarter = EndQuarterMutation.Field()


schema = graphene.Schema(query=Query, mutation=Mutation)
