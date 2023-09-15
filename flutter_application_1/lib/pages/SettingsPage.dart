import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';

import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:hive/hive.dart';
import 'package:url_launcher/url_launcher.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  final _myBox = Hive.box('token');
  final _themeBox = Hive.box('theme');

  _launchURL() async {
    final Uri url = Uri.parse(
        'https://docs.google.com/forms/d/e/1FAIpQLScHZbK-I3Wu6XnZ8DeDodbo9z04xgVaANg3YFjTmldd6xy0Wg/viewform');
    if (!await launchUrl(url)) {
      throw Exception('Could not launch $url');
    }
  }

  ThemeController _themes = ThemeController();

  final TextEditingController themeController = TextEditingController();
  String? selectedTheme;

  @override
  Widget build(BuildContext context) {
    selectedTheme = _themeBox.get('token');
    ThemeModel _theme = _themes.getTheme(_themeBox.get('token'));
    final List<DropdownMenuItem<String>> themeEntries =
        <DropdownMenuItem<String>>[];
    for (final String themeName in _themes.themes.keys) {
      themeEntries.add(DropdownMenuItem<String>(
        value: themeName,
        child: Text(themeName),
      ));
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(
          'Settings',
          style: GoogleFonts.notoSans(),
        ),
        shadowColor: _theme.secondary,
        backgroundColor: _theme.primary,
        foregroundColor: _theme.text,
      ),
      backgroundColor: _theme.background,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: EdgeInsets.only(top: 10, left: 20),
            child: Row(
              children: [
                Text(
                  "Theme: ",
                  style: GoogleFonts.notoSans(
                    textStyle: TextStyle(color: _theme.text, fontSize: 18),
                  ),
                ),
                DropdownButton<String>(
                  dropdownColor: _theme.secondary,
                  value: selectedTheme,
                  icon: Icon(
                    Icons.arrow_downward,
                    color: _theme.secondary,
                  ),
                  elevation: 16,
                  style: TextStyle(color: _theme.text),
                  underline: Container(
                    height: 2,
                    color: _theme.text,
                  ),
                  onChanged: (String? value) {
                    // This is called when the user selects an item.
                    setState(() {
                      selectedTheme = value;
                      _themeBox.put('token', value);
                      _theme = _themes.getTheme(_themeBox.get('token'));
                    });
                  },
                  items: themeEntries,
                ),
              ],
            ),
          ),
          Padding(
            padding: EdgeInsets.only(top: 10, left: 20),
            child: ElevatedButton(
              onPressed: _launchURL,
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.all(_theme.primary),
                foregroundColor: MaterialStateProperty.all(_theme.text),
                shape: MaterialStateProperty.all(
                  RoundedRectangleBorder(
                      side: BorderSide(color: _theme.secondary),
                      borderRadius: BorderRadius.all(Radius.circular(20))),
                ),

                // shadowColor: MaterialStateProperty.all(_theme.secondary),
              ),
              child: Text(
                'Need Extra Help?',
                style: GoogleFonts.notoSans(),
              ),
            ),
          ),
          Padding(
            padding: EdgeInsets.only(top: 10, left: 20),
            child: ElevatedButton(
              onPressed: () {
                _myBox.delete('token');

                Navigator.pushAndRemoveUntil(
                  context,
                  MaterialPageRoute(
                    builder: (context) => ValidationPage(),
                  ),
                  (Route<dynamic> route) => false,
                );
              },
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.all(_theme.primary),
                foregroundColor: MaterialStateProperty.all(_theme.text),
                shape: MaterialStateProperty.all(
                  RoundedRectangleBorder(
                      side: BorderSide(color: _theme.secondary),
                      borderRadius: BorderRadius.all(Radius.circular(20))),
                ),

                // shadowColor: MaterialStateProperty.all(_theme.secondary),
              ),
              child: Text(
                'Log Out',
                style: GoogleFonts.notoSans(),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
