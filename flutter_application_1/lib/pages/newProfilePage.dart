import 'package:flutter/material.dart';
import 'package:flutter_application_1/EventModel.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter_application_1/widgets/EventPreview.dart';
import 'package:flutter_application_1/widgets/newEventPreview.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:hive/hive.dart';
import 'package:qr_flutter/qr_flutter.dart';

class newProfile extends StatelessWidget {
  newProfile(
      {super.key,
      required this.student,
      required this.events,
      required this.theme});

  StudentModel student;

  List<EventModel> events;

  final _themeBox = Hive.box('theme');

  final ThemeModel theme;

  int points = 0;

  @override
  Widget build(BuildContext context) {
    var adjustedHeight = MediaQuery.of(context).size.height -
        kToolbarHeight -
        MediaQuery.of(context).padding.top -
        kBottomNavigationBarHeight;
    points = student.points;

    return Container(
      color: theme.background,
      constraints: const BoxConstraints.expand(),
      child: Column(
        children: <Widget>[
          SizedBox(
            height: adjustedHeight * 0.02,
          ),
          SizedBox(
            height: adjustedHeight * 0.1640,
            width: MediaQuery.of(context).size.width,
            child: FractionallySizedBox(
              widthFactor: 1,
              heightFactor: 1,
              child: Container(
                child: Center(
                  child: Column(
                    children: [
                      Text(
                        student.points.toString() + 'pts',
                        textAlign: TextAlign.center,
                        style: GoogleFonts.notoSans(
                          textStyle: TextStyle(
                              fontWeight: FontWeight.bold,
                              // fontSize: 80,
                              fontSize: adjustedHeight * 0.12,
                              color: theme.text),
                        ),
                      ),
                      // Text(
                      //   '${student.bonusPoints} bonus points',
                      //   style: TextStyle(fontSize: 10, color: Colors.green),
                      // )
                    ],
                  ),
                ),
              ),
            ),
          ),
          SizedBox(
            height: adjustedHeight * 0.02,
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(adjustedHeight * 0.028),
              color: theme.primary,
              border: Border.all(color: theme.text, width: 2),
            ),
            child: IconButton(
              iconSize: adjustedHeight * 0.08,
              padding: EdgeInsets.zero,
              color: theme.text,
              constraints: BoxConstraints(),
              icon: Icon(Icons.qr_code_2_rounded),
              onPressed: () {
                showDialog(
                  context: context,
                  builder: (BuildContext context) => AlertDialog(
                    content: SizedBox(
                      width: 500,
                      height: 260,
                      child: QrImage(
                        data: student.studentId.toString(),
                        version: QrVersions.auto,
                        size: 100,
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
          SizedBox(
            height: adjustedHeight * 0.03,
          ),
          Expanded(
            child: Container(
              height: adjustedHeight * 0.69,
              width: MediaQuery.of(context).size.width,
              decoration: BoxDecoration(
                  color: theme.primary,
                  gradient: LinearGradient(
                      stops: [0, 0.5],
                      colors: [theme.primary, theme.secondary],
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter),
                  // border: Border(
                  //     top: BorderSide(color: theme.secondary, width: 2),
                  //     left: BorderSide(color: theme.secondary, width: 2),
                  //     right: BorderSide(color: theme.secondary, width: 2),
                  //     bottom: BorderSide(color: theme.secondary, width: 2)),
                  borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(20),
                      topRight: Radius.circular(20)),
                  boxShadow: [BoxShadow(color: theme.secondary, blurRadius: 20)]

                  // border:
                  //     Border(top: BorderSide(color: theme.secondary, width: 2)),
                  // borderRadius: BorderRadius.circular(20),
                  ),
              child: Column(
                children: [
                  Align(
                    alignment: Alignment.topCenter,
                    child: Padding(
                      padding: EdgeInsets.only(
                          top: adjustedHeight * 0.01,
                          left: 0.0,
                          bottom: 0.0,
                          right: 0.0),
                      child: Text(
                        'Your Events',
                        style: GoogleFonts.notoSans(
                          textStyle: TextStyle(
                            fontSize: adjustedHeight * 0.04,
                            color: theme.text,
                          ),
                          fontWeight: FontWeight.w800,
                        ),
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
                        height: adjustedHeight * 0.76 * 0.95 - 75,
                        child: events.isNotEmpty
                            ? ListView.builder(
                                shrinkWrap: true,
                                itemCount: events.length,
                                itemBuilder: (context, index) {
                                  // print(events.isEmpty);

                                  return newEventPreview(
                                    event_name: events[index].name,
                                    points: events[index].points,
                                    hours: (events[index]
                                            .endTime
                                            .difference(events[index].startTime)
                                            .inMinutes) /
                                        60,
                                    theme: theme,
                                    elapsed: events[index].elapsed,
                                  );
                                },
                              )
                            : SizedBox(),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
