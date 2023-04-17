import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:flutter/material.dart';

class ThemeController {
  static ThemeModel defaultTheme = ThemeModel(
      primary: Color.fromARGB(255, 214, 214, 214),
      secondary: Colors.grey,
      text: Colors.black,
      background: Colors.white,
      icon: Colors.black);

  final Map<String, ThemeModel> themes = {
    "light": ThemeModel(
        primary: Color.fromARGB(255, 214, 214, 214),
        secondary: Colors.grey,
        text: Colors.black,
        background: Colors.white,
        icon: Colors.black),
    "dark": ThemeModel(
        primary: Color.fromARGB(255, 38, 38, 38),
        secondary: Color.fromARGB(255, 93, 93, 93),
        text: Colors.white,
        background: Color.fromARGB(255, 54, 54, 54),
        icon: Colors.white)
  };

  ThemeModel getTheme(String? theme) {
    if (themes.containsKey(theme)) {
      ThemeModel activeTheme = themes[theme]!;
      return activeTheme;
    }
    return defaultTheme;
  }
}
