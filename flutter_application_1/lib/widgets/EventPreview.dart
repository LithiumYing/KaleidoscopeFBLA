import 'dart:ffi';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

class EventPreview extends StatelessWidget {
  EventPreview(
      {super.key,
      this.event_name = 'place_holder',
      this.hours = 0,
      this.points = 0,
      required this.theme,
      required this.elapsed});

  final String event_name;
  final double hours;
  final int points;

  final ThemeModel theme;

  final bool elapsed;

  String truncateWithEllipsis(int cutoff, String myString) {
    return (myString.length <= cutoff)
        ? myString
        : '${myString.substring(0, cutoff)}...';
  }

  @override
  Widget build(BuildContext context) {
    // print(event_name);

    String modified_event_name;
    if (event_name.length > 10) {
      modified_event_name = event_name.substring(0, 10) + '.';
    } else {
      modified_event_name = event_name;
    }

    return event_name == ""
        ? Container()
        : Container(
            child: SizedBox(
              child: Column(
                children: [
                  Align(
                    alignment: Alignment.centerLeft,
                    child: Padding(
                      padding: EdgeInsets.only(
                          top: 12.0, left: 12.0, bottom: 0.0, right: 0.0),
                      child: Row(children: [
                        SizedBox(
                          width: 150,
                          child: Text(
                            modified_event_name,
                            style: GoogleFonts.robotoMono(
                                textStyle: TextStyle(
                                    fontSize: 20,
                                    fontWeight: FontWeight.w500,
                                    color: theme.text)),
                          ),
                        ),
                        SizedBox(
                          child: Text(
                            '${double.parse((hours).toStringAsFixed(2))}hrs',
                            style: TextStyle(fontSize: 20, color: theme.text),
                          ),
                        ),
                        Expanded(
                          child: Padding(
                            padding: EdgeInsets.only(right: 20),
                            child: Text(
                              '${points}pts',
                              textAlign: TextAlign.right,
                              style: TextStyle(
                                  fontSize: 20,
                                  color: elapsed ? theme.text : Colors.red),
                            ),
                          ),
                        ),
                      ]),
                    ),
                  ),
                  Divider(
                    thickness: 2,
                    color: theme.text,
                    indent: 10,
                    endIndent: 10,
                  ),
                ],
              ),
            ),
          );
  }
}
