import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter/material.dart';

class ThemeController {

  /* The default theme of the app is light theme
     It is defined seperately because dart does not allow a reference to
     the themes map inside the initializer
  */
  static ThemeModel defaultTheme = ThemeModel(
      primary: Color.fromARGB(255, 214, 214, 214),
      secondary: Colors.grey,
      text: Colors.black,
      text2: Color.fromARGB(255, 37, 37, 37),
      background: Colors.white,
      icon: Colors.black,
      freshmen: Colors.blue,
      sophomores: Colors.red,
      juniors: Colors.orange,
      seniors: Colors.green);

  /*
    All themes are stored here, if you want to add or edit themes it is as
    easy as modifying the key value pairs in this map
  */
  final Map<String, ThemeModel> themes = {
    "light": ThemeModel(
        primary: Color.fromARGB(255, 214, 214, 214),
        secondary: Colors.grey,
        text: Colors.black,
        text2: Color.fromARGB(255, 37, 37, 37),
        background: Colors.white,
        icon: Colors.black,
        freshmen: Colors.blue,
        sophomores: Colors.red,
        juniors: Colors.orange,
        seniors: Colors.green),
    "dark": ThemeModel(
        primary: Color.fromARGB(255, 38, 38, 38),
        secondary: Color.fromARGB(255, 68, 68, 68),
        text: Colors.white,
        text2: Color.fromARGB(255, 157, 157, 157),
        background: Color.fromARGB(255, 54, 54, 54),
        icon: Colors.white,
        freshmen: Color.fromARGB(231, 78, 111, 255),
        sophomores: Color.fromARGB(213, 255, 55, 55),
        juniors: Color.fromARGB(255, 252, 133, 41),
        seniors: Color.fromARGB(255, 87, 167, 53)),
  };

  /* Used to grap a theme from the theme map. Usually this is done through
    cached strings. Check implementation in other files for examples.
  */
  ThemeModel getTheme(String? theme) {
    if (themes.containsKey(theme)) {
      ThemeModel activeTheme = themes[theme]!;
      return activeTheme;
    }
    return defaultTheme;
  }
}
