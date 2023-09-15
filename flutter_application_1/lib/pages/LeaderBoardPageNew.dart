import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter_application_1/widgets/LeaderboardSegment.dart';
import 'package:flutter_application_1/widgets/NewLeaderboardSegment.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:hive/hive.dart';

import '../widgets/topLeaderboardSegment.dart';

class newLeaderboard extends StatelessWidget {
  StudentModel student;
  List<StudentModel> students;

  newLeaderboard(
      {super.key,
      required this.student,
      required this.students,
      required this.theme});

  ThemeModel theme;
  final _myBox = Hive.box('token');

  final seniorGradYear = 2023;

  final List<StudentModel> topStudentsByGraduation = [
    StudentModel.empty(),
    StudentModel.empty(),
    StudentModel.empty(),
    StudentModel.empty()
  ];

  final String studentWithMostPointsByGraduationYear = """
  query MyQuery (\$gradYear: Int) {
  studentWithMaxPointsByGraduationYear(graduationYear: \$gradYear) {
    studentId
        lastName
        firstName
        bonusPoints {
          points
        }
        rank
        graduationYear
        eventsAttended
        email
        qrcode
        points
        prizes{
          id
          name
          description
          image
        }
  }
}
""";

  Widget topStudentOfGradYear(
      {required Widget child,
      required int seniorGradYear,
      required int gradYearAdjustor}) {
    return Query(
      options: QueryOptions(
        document: gql(studentWithMostPointsByGraduationYear),
        variables: {'gradYear': seniorGradYear + gradYearAdjustor},
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        Map raw = result.data!['studentWithMaxPointsByGraduationYear'];
        // print('p  $raw');
        if (raw == Null || raw.isEmpty) {
          return SizedBox(
            height: 100,
            child: Container(
              color: Colors.red,
              child: Text('Please Try Again'),
            ),
          );
          ;
        }

        topStudentsByGraduation[gradYearAdjustor] =
            (StudentModel.fromMap(map: raw));

        return child;
      },
    );
  }

  Widget topStudentsPerYear(
      {required int seniorGradYear, required Widget child}) {
    return topStudentOfGradYear(
        child: topStudentOfGradYear(
            child: topStudentOfGradYear(
                child: topStudentOfGradYear(
                    child: child,
                    seniorGradYear: seniorGradYear,
                    gradYearAdjustor: 3),
                seniorGradYear: seniorGradYear,
                gradYearAdjustor: 2),
            seniorGradYear: seniorGradYear,
            gradYearAdjustor: 1),
        seniorGradYear: seniorGradYear,
        gradYearAdjustor: 0);
  }

  @override
  Widget build(BuildContext context) {
    var adjustedHeight = MediaQuery.of(context).size.height -
        kToolbarHeight -
        MediaQuery.of(context).padding.top -
        kBottomNavigationBarHeight;
    return topStudentsPerYear(
      seniorGradYear: seniorGradYear,
      child: Container(
        color: theme.background,
        constraints: BoxConstraints.expand(),
        child: Column(
          children: <Widget>[
            Container(
              padding: EdgeInsets.only(
                  top: adjustedHeight * 0.015, bottom: adjustedHeight * 0.005),
              child: Text(
                'Top Scores',
                style: GoogleFonts.notoSans(
                    textStyle: TextStyle(
                        fontSize: adjustedHeight * 0.035,
                        fontWeight: FontWeight.w800),
                    color: theme.text),
              ),
            ),
            Divider(
              height: 2,
              color: theme.text,
              thickness: adjustedHeight * 0.003,
              indent: MediaQuery.of(context).size.width * 0.05,
              endIndent: MediaQuery.of(context).size.width * 0.05,
            ),
            Container(
              padding: EdgeInsets.only(top: adjustedHeight * 0.01),
              height: adjustedHeight * 0.353,
              width: MediaQuery.of(context).size.width * 0.9,
              child: ListView.builder(
                itemCount: topStudentsByGraduation.length,
                itemBuilder: (context, index) {
                  return Padding(
                    padding: EdgeInsets.only(bottom: adjustedHeight * 0.012),
                    child: Material(
                      elevation: 10,
                      borderRadius: BorderRadius.circular(20),
                      child: topLeaderboardSegment(
                        theme: theme,
                        height: adjustedHeight * 0.07,
                        seniorGradYear: 2023,
                        student: topStudentsByGraduation[index],
                      ),
                    ),
                  );
                },
              ),
            ),
            Expanded(
              child: Container(
                decoration: BoxDecoration(
                  color: theme.primary,
                  // boxShadow: [
                  //   BoxShadow(
                  //       offset: Offset(5, -10),
                  //       color: theme.secondary,
                  //       blurRadius: adjustedHeight * 0.04)
                  // ],
                  borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(20),
                      topRight: Radius.circular(20)),
                  gradient: LinearGradient(
                      stops: [0, 0.5],
                      colors: [theme.primary, theme.secondary],
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter),
                  // borderRadius: BorderRadius.circular(10),
                ),
                // TODO: I might be able to eliminate the two container system and just move the height to the one on top!
                child: Container(
                  height: adjustedHeight * 0.4,
                  width: MediaQuery.of(context).size.width,
                  child: Column(
                    children: [
                      Text(
                        "Leader Board",
                        style: GoogleFonts.notoSans(
                          textStyle: TextStyle(
                              color: theme.text,
                              fontWeight: FontWeight.w800,
                              fontSize: adjustedHeight * 0.035),
                        ),
                      ),
                      Divider(
                        height: 2,
                        color: theme.text,
                        thickness: adjustedHeight * 0.003,
                        indent: MediaQuery.of(context).size.width * 0.05,
                        endIndent: MediaQuery.of(context).size.width * 0.05,
                      ),
                      SizedBox(
                        height: adjustedHeight * 0.4,
                        child: ListView.builder(
                          shrinkWrap: true,
                          itemCount: students.length,
                          itemBuilder: (context, index) {
                            return Container(
                              padding: EdgeInsets.only(top: 2),
                              alignment: Alignment.center,
                              child: newLeaderboardSegment(
                                theme: theme,
                                index: index,
                                student: students[index],
                                seniorGradYear: 2023,
                                height: adjustedHeight * 0.052,
                              ),
                            );
                          },
                        ),
                      ),
                      Divider(
                        height: 2,
                        color: theme.text,
                        thickness: adjustedHeight * 0.004,
                        indent: MediaQuery.of(context).size.width * 0.05,
                        endIndent: MediaQuery.of(context).size.width * 0.05,
                      ),
                      Container(
                        // height: adjustedHeight * 0.074,
                        width: MediaQuery.of(context).size.width,
                        alignment: Alignment.center,
                        child: newLeaderboardSegment(
                          theme: theme,
                          height: adjustedHeight * 0.057,
                          seniorGradYear: 2023,
                          isYou: true,
                          student: student,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
