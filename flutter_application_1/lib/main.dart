import 'package:cache_manager/core/cache_manager_utils.dart';
import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter/material.dart';
import 'package:flutter_application_1/EventModel.dart';
import 'package:flutter_application_1/StudentModel.dart';

import 'package:flutter_application_1/pages/LogInPage.dart';
import 'package:flutter_application_1/pages/MainScaffold.dart';
import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:flutter_application_1/pages/ProfilePage.dart';
import 'package:flutter_application_1/pages/LeaderBoardPage.dart';
import 'package:flutter_application_1/pages/CalendarPage.dart';
import 'package:flutter_application_1/pages/SettingsPage.dart';

import 'package:qr_flutter/qr_flutter.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:hive_flutter/hive_flutter.dart';

void main() async {
  await Hive.initFlutter();

  var box = await Hive.openBox('token');
  var themeBox = await Hive.openBox('theme');

  await initHiveForFlutter();

  final HttpLink httpLink = HttpLink(
    'https://kaleidoscope-fbla.herokuapp.com/graphql/',
    defaultHeaders: {
      "Authorization": box.get('token').toString(),
    },
  );

  ValueNotifier<GraphQLClient> client = ValueNotifier(
    GraphQLClient(
      link: httpLink,
      cache: GraphQLCache(store: HiveStore()),
    ),
  );

  runApp(MyApp(
    client: client,
  ));
}

class MyApp extends StatelessWidget {
  MyApp({super.key, required this.client});

  static const String _title = 'FBLA APP';

  ValueNotifier<GraphQLClient> client;

  @override
  Widget build(BuildContext context) {
    return GraphQLProvider(
        client: client,
        child: MaterialApp(
          debugShowCheckedModeBanner: false,
          title: _title,
          home: MyPages(),
        ));
  }
}

class MyPages extends StatefulWidget {
  const MyPages({super.key});

  @override
  State<MyPages> createState() => _MyPagesState();
}

class _MyPagesState extends State<MyPages> {
  @override
  Widget build(BuildContext context) {
    final List<Widget> logIn = <Widget>[LogInPage(), MainScaffold()];

    int _logInIndex = 1;

    // return logIn.elementAt(_logInIndex);
    return ValidationPage();
  }
}
