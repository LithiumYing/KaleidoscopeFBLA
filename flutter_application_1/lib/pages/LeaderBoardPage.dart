import 'package:flutter/material.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter_application_1/widgets/LeaderboardSegment.dart';

class Leaderboard extends StatelessWidget {
  StudentModel student;
  List<StudentModel> students;

  Leaderboard(
      {super.key,
      required this.student,
      required this.students,
      required this.theme});

  ThemeModel theme;

  @override
  Widget build(BuildContext context) {
    var adjustedHeight = MediaQuery.of(context).size.height -
        kToolbarHeight -
        MediaQuery.of(context).padding.top -
        kBottomNavigationBarHeight;
    return Container(
      color: theme.background,
      constraints: BoxConstraints.expand(),
      child: Column(
        children: <Widget>[
          SizedBox(
            height: adjustedHeight * 0.2,
            child: FractionallySizedBox(
              widthFactor: 0.9,
              heightFactor: 0.8,
              child: Container(
                decoration: BoxDecoration(
                  border: Border.all(color: theme.secondary, width: 2),
                  color: theme.primary,
                  borderRadius: BorderRadius.all(Radius.circular(20)),
                ),
                child: Column(children: [
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: EdgeInsets.only(left: 20, top: 10),
                      child: Text(
                        'Your Position:',
                        textAlign: TextAlign.left,
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 30,
                            color: theme.text),
                      ),
                    ),
                  ),
                  LeaderboardSegment(
                    theme: theme,
                    rank: student.rank,
                    points: student.points,
                    name: '${student.firstName} ${student.lastName}',
                  )
                ]),
              ),
            ),
          ),
          SizedBox(
            height: adjustedHeight * 0.764,
            width: MediaQuery.of(context).size.width,
            child: FractionallySizedBox(
              widthFactor: 0.95,
              heightFactor: 0.95,
              // height: 100,
              child: DecoratedBox(
                decoration: BoxDecoration(
                  // border: Border.all(color: theme.secondary, width: 2),
                  color: theme.primary,
                  border: Border.all(color: theme.secondary, width: 2),
                  borderRadius: BorderRadius.all(Radius.circular(20)),
                ),
                child: Column(
                  children: <Widget>[
                    Align(
                      alignment: Alignment.topLeft,
                      child: Padding(
                        padding: EdgeInsets.only(
                            top: 12.0, left: 12.0, bottom: 0.0, right: 0.0),
                        child: Text(
                          'Leaderboard:',
                          style: TextStyle(fontSize: 35, color: theme.text),
                        ),
                      ),
                    ),
                    Divider(
                      thickness: 3,
                      color: theme.text,
                      indent: 5,
                      endIndent: 5,
                    ),
                    Expanded(
                      child: SingleChildScrollView(
                        child: SizedBox(
                          height: adjustedHeight * 0.764 * 0.95 - 80,
                          child: ListView.builder(
                            shrinkWrap: true,
                            itemCount: students.length,
                            itemBuilder: (context, index) {
                              return LeaderboardSegment(
                                theme: theme,
                                rank: index + 1,
                                points: students[index].points,
                                name:
                                    '${students[index].firstName} ${students[index].lastName}',
                              );
                            },
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
