import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

class LeaderboardSegment extends StatelessWidget {
  const LeaderboardSegment(
      {super.key,
      this.rank = 0,
      this.points = 0,
      this.name = 'John Doe',
      required this.theme});

  final int rank;
  final int points;
  final String name;

  final ThemeModel theme;

  @override
  Widget build(BuildContext context) {
    // print(rank);
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
    return Container(
      child: SizedBox(
        child: Column(
          children: [
            Align(
              alignment: Alignment.centerLeft,
              child: Padding(
                padding: EdgeInsets.only(
                    top: 12.0, left: 12.0, bottom: 0.0, right: 0.0),
                child: IntrinsicHeight(
                  child: Row(
                    children: [
                      Text(
                        rank.toString(),
                        style: TextStyle(fontSize: 20, color: theme.text),
                      ),
                      VerticalDivider(
                        color: theme.text,
                        thickness: 2,
                        indent: 3.5,
                        endIndent: 3.5,
                      ),
                      SizedBox(
                        width: 170,
                        child: Text(
                          name_adjusted,
                          style: GoogleFonts.robotoMono(
                              textStyle: TextStyle(
                                  fontSize: 20,
                                  fontWeight: FontWeight.w500,
                                  color: theme.text)),
                        ),
                      ),
                      Expanded(
                        child: Padding(
                          padding: EdgeInsets.only(right: 20),
                          child: Text(
                            '${points}pts',
                            textAlign: TextAlign.right,
                            style: TextStyle(fontSize: 20, color:  theme.text),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
            Divider(
              thickness: 2,
              color: theme.text,
              indent: 5,
              endIndent: 5,
            ),
          ],
        ),
      ),
    );
  }
}
