import 'package:date_format/date_format.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

class newLeaderboardSegment extends StatelessWidget {
  const newLeaderboardSegment(
      {super.key,
      required this.student,
      this.height = 170,
      required this.seniorGradYear,
      required this.theme,
      this.index = 0,
      this.isYou = false});

  final StudentModel student;
  final double height;
  final int seniorGradYear;
  final ThemeModel theme;
  final bool isYou;
  final int index;

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
      gradYearString = "Se";
    } else if (student.graduationYear == seniorGradYear + 1) {
      gradeColor = theme.juniors;
      gradYearString = "Ju";
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
      height: height,
      width: isYou
          ? MediaQuery.of(context).size.width * 0.9
          : MediaQuery.of(context).size.width * 0.87,
      alignment: Alignment.center,
      decoration: BoxDecoration(
          // gradient: LinearGradient(
          //     stops: [0.7, isYou ? 1 : 0.91, isYou ? 1 : .9],
          //     colors: [Colors.transparent, gradeColor, Colors.transparent],
          //     begin: Alignment.topCenter,
          //     end: Alignment.bottomCenter),
          ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(
            children: [
              Text('${index + 1}   ',
                  style: GoogleFonts.notoSans(
                    textStyle: TextStyle(
                      fontSize: height * 0.6,
                      fontWeight: FontWeight.w500,
                      color: theme.text,
                      // shadows: [Shadow(color: gradeColor, blurRadius: 20)],
                    ),
                  )),
              isYou
                  ? Text('${name_adjusted} (You)',
                      style: GoogleFonts.notoSans(
                          textStyle: TextStyle(
                              fontSize: height * 0.6,
                              fontWeight: FontWeight.w500,
                              color: theme.text)))
                  : Text('${name_adjusted}',
                      style: GoogleFonts.notoSans(
                        textStyle: TextStyle(
                            fontSize: height * 0.6,
                            fontWeight: FontWeight.w500,
                            color: theme.text),
                        // shadows: [Shadow(color: gradeColor, blurRadius: 10)],
                      )),
              Expanded(
                child: Text(
                  '${student.points}pts',
                  textAlign: TextAlign.right,
                  style: GoogleFonts.notoSans(
                    textStyle: TextStyle(
                      fontSize: height * 0.6,
                      color: theme.text,
                      shadows: [Shadow(color: gradeColor, blurRadius: 10)],
                    ),
                  ),
                ),
              ),
            ],
          ),
          !isYou
              ? Divider(
                  height: 2,
                  color: theme.text,
                  thickness: height * 0.04,
                  // indent: MediaQuery.of(context).size.width * 0.03,
                  // endIndent: MediaQuery.of(context).size.width * 0.03,
                )
              : SizedBox(),
        ],
      ),
    );
  }
}
