import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:hive/hive.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({super.key});

  @override
  State<SettingsPage> createState() => _SettingsPageState();
}

class _SettingsPageState extends State<SettingsPage> {
  final _myBox = Hive.box('token');
  final _themeBox = Hive.box('theme');

  ThemeController _themes = ThemeController();

  final TextEditingController themeController = TextEditingController();
  String? selectedTheme;

  @override
  Widget build(BuildContext context) {
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
        title: const Text('Settings'),
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
                  style: TextStyle(color: _theme.text, fontSize: 18),
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
              child: const Text('Log Out'),
            ),
          ),
        ],
      ),
    );
  }
}
