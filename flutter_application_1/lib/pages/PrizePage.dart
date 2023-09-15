import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:hive/hive.dart';

class PrizePage extends StatelessWidget {
  PrizePage({super.key, required this.student});

  final StudentModel student;

  ThemeController _themeController = ThemeController();
  final _themeBox = Hive.box('theme');

  final String queryAailablePrizes = """
    query{
      availablePrizes{
        id
        name
        description
        image
        student{
          lastName
          firstName
        }
      }
    }
""";

  @override
  Widget build(BuildContext context) {
    // print(student);
    ThemeModel _theme = _themeController.getTheme(_themeBox.get('token'));
    var adjustedHeight = MediaQuery.of(context).size.height -
        kToolbarHeight -
        MediaQuery.of(context).padding.top;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Prizes',
          style: GoogleFonts.notoSans(),
        ),
        shadowColor: _theme.secondary,
        foregroundColor: _theme.text,
        backgroundColor: _theme.primary,
      ),
      backgroundColor: _theme.background,
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Center(
            child: Padding(
              padding: EdgeInsets.only(top: adjustedHeight * 0.03),
              child: Text(
                'Available Prizes',
                textAlign: TextAlign.center,
                style: GoogleFonts.notoSans(
                  textStyle: TextStyle(
                      fontSize: adjustedHeight * 0.03, color: _theme.text),
                ),
              ),
            ),
          ),
          Divider(
            height: 2,
            color: _theme.text,
            indent: 50,
            endIndent: 50,
          ),
          SizedBox(
            height: adjustedHeight * 0.40,
            child: Query(
              options: QueryOptions(
                  document: gql(queryAailablePrizes),
                  pollInterval: const Duration(seconds: 10)),
              builder: (result, {fetchMore, refetch}) {
                if (result.hasException) {
                  return Text(result.exception.toString());
                }

                if (result.isLoading) {
                  return const CircularProgressIndicator();
                }

                List raw = result.data!['availablePrizes'];

                // print(raw);
                // print(raw[0]['image'].toString());
                // print(raw[0]['name'].toString());
                // print(raw[0]['description'].toString());
                // print(raw[1]['name'].toString());

                return SingleChildScrollView(
                  child: SizedBox(
                    height: adjustedHeight * 0.45,
                    child: ListView.builder(
                      shrinkWrap: true,
                      itemCount: raw.length,
                      itemBuilder: (context, index) {
                        return ListTile(
                          leading: raw[index]['image'].runtimeType != String
                              ? Icon(
                                  Icons.card_giftcard_rounded,
                                )
                              : Image.network(
                                  raw[index]['image'].toString(),
                                  width: 60,
                                  height: 100,
                                  fit: BoxFit.fill,
                                ),
                          title: Text(
                            raw[index]['name'].toString(),
                            style: GoogleFonts.notoSans(
                              textStyle: TextStyle(
                                color: _theme.text,
                              ),
                            ),
                          ),
                          subtitle: Text(
                            raw[index]['description'].toString(),
                            style: GoogleFonts.notoSans(
                              textStyle: TextStyle(
                                color: _theme.text,
                              ),
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                );
              },
            ),
          ),
          Center(
            child: Padding(
              padding: EdgeInsets.only(top: adjustedHeight * 0.03),
              child: Text(
                'Your Prizes',
                textAlign: TextAlign.center,
                style: GoogleFonts.notoSans(
                  textStyle: TextStyle(
                    fontSize: adjustedHeight * 0.03,
                    color: _theme.text,
                  ),
                ),
              ),
            ),
          ),
          Divider(
            height: 2,
            color: _theme.text,
            indent: 50,
            endIndent: 50,
          ),
          SingleChildScrollView(
            child: SizedBox(
              height: adjustedHeight * 0.41,
              child: ListView.builder(
                itemCount: student.prizes.length,
                itemBuilder: (context, index) {
                  return ListTile(
                    leading:
                        student.prizes[index]['image'].runtimeType != String
                            ? Icon(
                                Icons.card_giftcard_rounded,
                              )
                            : Image.network(
                                student.prizes[index]['image'].toString(),
                                width: 60,
                                height: 100,
                                fit: BoxFit.fill,
                              ),
                    title: Text(
                      student.prizes[index]['name'].toString(),
                      style: GoogleFonts.notoSans(
                        textStyle: TextStyle(
                          color: _theme.text,
                        ),
                      ),
                    ),
                    subtitle: Text(
                      student.prizes[index]['description'].toString(),
                      style: GoogleFonts.notoSans(
                        textStyle: TextStyle(
                          color: _theme.text,
                        ),
                      ),
                    ),
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
