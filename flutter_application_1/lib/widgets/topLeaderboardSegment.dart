import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

class topLeaderboardSegment extends StatelessWidget {
  const topLeaderboardSegment({
    super.key,
    required this.student,
    this.height = 170,
    required this.seniorGradYear,
    required this.theme,
    this.isYou = false,
  });

  final StudentModel student;
  final double height;
  final int seniorGradYear;
  final ThemeModel theme;
  final bool isYou;

  @override
  Widget build(BuildContext context) {
    String name = (student.firstName + " " + student.lastName);
    List name_splitted = name.split(' ');
    String name_adjusted;
    if (name.length > 14) {
      name_adjusted = name_splitted[0].toString() +
          " " +
          name_splitted[name_splitted.length - 1].toString()[0] +
          ".";
    } else {
      name_adjusted = name;
    }

    Color gradeColor;
    String gradYearString;
    if (student.graduationYear == seniorGradYear) {
      gradeColor = theme.seniors;
      gradYearString = "Sr";
    } else if (student.graduationYear == seniorGradYear + 1) {
      gradeColor = theme.juniors;
      gradYearString = "Jr";
    } else if (student.graduationYear == seniorGradYear + 2) {
      gradeColor = theme.sophomores;
      gradYearString = "So";
    } else if (student.graduationYear == seniorGradYear + 3) {
      gradeColor = theme.freshmen;
      gradYearString = "Fr";
    } else {
      gradeColor = theme.icon;
      gradYearString = "Other";
    }

    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(20),
        color: theme.primary,
        border: Border.all(color: theme.secondary, width: 2),
        gradient: LinearGradient(
            stops: [0.6, 1],
            colors: [theme.background, gradeColor],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter),
      ),
      height: height,
      // width: MediaQuery.of(context).size.width * 0.8,
      alignment: Alignment.centerLeft,
      padding: EdgeInsets.only(top: 0, left: 12.0, bottom: 0.0, right: 0.0),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            children: [
              Text(isYou ? '${student.rank}   ' : '${gradYearString}   ',
                  style: GoogleFonts.notoSans(
                      textStyle: TextStyle(
                          fontSize: height * 0.43,
                          fontWeight: FontWeight.w500,
                          color: theme.text))),
              Text(isYou ? 'You' : '${name_adjusted}',
                  style: GoogleFonts.notoSans(
                      textStyle: TextStyle(
                          fontSize: height * 0.43,
                          fontWeight: FontWeight.w500,
                          color: theme.text))),
              Expanded(
                child: Padding(
                  padding: EdgeInsets.only(right: 20),
                  child: Text(
                    '${student.points}pts',
                    textAlign: TextAlign.right,
                    style: GoogleFonts.notoSans(
                      textStyle:
                          TextStyle(fontSize: height * 0.43, color: theme.text),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
