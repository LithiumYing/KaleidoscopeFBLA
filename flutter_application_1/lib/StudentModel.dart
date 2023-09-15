class StudentModel {
  final int studentId;
  final String lastName;
  final String firstName;
  final int graduationYear;
  final int eventsAttended;
  final String email;
  final String qrcode;
  final int points;
  final int bonusPoints;
  final int rank;
  final List prizes;

  StudentModel(
      {required this.studentId,
      required this.lastName,
      required this.firstName,
      required this.graduationYear,
      required this.eventsAttended,
      required this.email,
      required this.qrcode,
      required this.bonusPoints,
      required this.points,
      required this.rank,
      required this.prizes});

  static StudentModel fromMap({required Map map}) {
    int bonusPoints = 0;
    try {
      List<Map> bonusPointsMap = map['bonusPoints'];

      for (Map bonusPoint in bonusPointsMap) {
        bonusPoints += int.parse(bonusPoint['points']);
      }
    } catch (e) {
      bonusPoints = 0;
      // print(Exception(e));
    }

    StudentModel student = StudentModel(
      studentId: map['studentId'],
      firstName: map['firstName'],
      lastName: map['lastName'],
      graduationYear: map['graduationYear'],
      eventsAttended: map['eventsAttended'],
      email: map['email'],
      rank: map['rank'],
      qrcode: map['qrcode'],
      points: map['points'],
      prizes: map['prizes'],
      bonusPoints: bonusPoints,
    );

    return student;
  }

  static StudentModel empty() {
    return StudentModel(
        studentId: 0,
        rank: 0,
        firstName: "John",
        lastName: "Doe",
        graduationYear: 0,
        eventsAttended: 0,
        email: "",
        qrcode: "",
        points: 0,
        bonusPoints: 0,
        prizes: []);
  }
}
