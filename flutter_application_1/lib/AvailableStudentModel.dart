class AvailableStudentModel {
  final int studentId;
  final String lastName;
  final String firstName;
  final int graduationYear;
  final bool isReal;

  AvailableStudentModel({
    required this.studentId,
    required this.lastName,
    required this.firstName,
    required this.graduationYear,
    required this.isReal
  });

  static AvailableStudentModel fromMap({required Map map}) {
    AvailableStudentModel student = AvailableStudentModel(
      studentId: map['studentId'],
      firstName: map['firstName'],
      lastName: map['lastName'],
      graduationYear: map['graduationYear'],
      isReal: true
    );

    return student;
  }

  static AvailableStudentModel empty() {
    return AvailableStudentModel(
      studentId: 0,
      firstName: "John",
      lastName: "Doe",
      graduationYear: 0,
      isReal: false
    );
  }
}
