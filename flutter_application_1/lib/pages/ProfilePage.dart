import 'package:flutter/material.dart';
import 'package:flutter_application_1/EventModel.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter_application_1/widgets/EventPreview.dart';
import 'package:hive/hive.dart';

class Profile extends StatelessWidget {
  Profile(
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
    points = student.points;
    return Container(
      color: theme.background,
      constraints: const BoxConstraints.expand(),
      child: Column(
        children: <Widget>[
          SizedBox(
            height: 200,
            child: FractionallySizedBox(
              widthFactor: 1,
              heightFactor: 0.8,
              child: Container(
                child: Center(
                  child: Column(
                    children: [
                      Text(
                        student.points.toString() + 'pts',
                        textAlign: TextAlign.center,
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: 80,
                            color: theme.text),
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
          Expanded(
            child: FractionallySizedBox(
              widthFactor: 0.9,
              heightFactor: 0.95,
              child: DecoratedBox(
                decoration: BoxDecoration(
                  color: theme.primary,
                  border: Border.all(color: theme.secondary, width: 2),
                  borderRadius: BorderRadius.circular(20),
                ),
                child: Column(
                  children: [
                    Align(
                      alignment: Alignment.topLeft,
                      child: Padding(
                        padding: EdgeInsets.only(
                            top: 12.0, left: 12.0, bottom: 0.0, right: 0.0),
                        child: Text(
                          'Your Events:',
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
                        child: Column(
                          children: <Widget>[
                            events.isNotEmpty
                                ? ListView.builder(
                                    shrinkWrap: true,
                                    itemCount: events.length,
                                    itemBuilder: (context, index) {
                                      // print(events.isEmpty);
                                      print(events);
                                      return EventPreview(
                                        event_name: events[index].name,
                                        points: events[index].points,
                                        hours: (events[index]
                                                .endTime
                                                .difference(
                                                    events[index].startTime)
                                                .inMinutes) /
                                            60,
                                        theme: theme,
                                      );
                                    },
                                  )
                                : SizedBox()
                          ],
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
