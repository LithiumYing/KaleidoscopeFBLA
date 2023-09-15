import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/EventModel.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:flutter_application_1/pages/CalendarPage.dart';
import 'package:flutter_application_1/pages/LeaderBoardPage.dart';
import 'package:flutter_application_1/pages/LogInPage.dart';
import 'package:flutter_application_1/pages/PrizePage.dart';
import 'package:flutter_application_1/pages/ProfilePage.dart';
import 'package:flutter_application_1/pages/SettingsPage.dart';
import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:flutter_application_1/pages/helpPage.dart';
import 'package:flutter_application_1/pages/newProfilePage.dart';
import 'package:flutter_application_1/themes/ThemeController.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:hive/hive.dart';

import 'LeaderBoardPageNew.dart';

class MainScaffold extends StatefulWidget {
  const MainScaffold({super.key});

  @override
  State<MainScaffold> createState() => _MainScaffoldState();
}

class _MainScaffoldState extends State<MainScaffold> {
  int id = 24230;

  int _selectedIndex = 1;

  final _myBox = Hive.box('token');
  final _themeBox = Hive.box('theme');

  ThemeController _themeController = ThemeController();

  void _onItemTapped(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  List<StudentModel> _students = [StudentModel.empty()];

  List<EventModel> _studentEvents = [EventModel.empty()];

  List<EventModel> _events = [EventModel.empty()];

  StudentModel _student = StudentModel.empty();

  Widget students(Widget child) {
    return Query(
      options: QueryOptions(
        document: gql(queryStudents),
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        List raw = result.data!['students'];

        if (raw == Null || raw.isEmpty) {
          return child;
        }

        _students =
            raw.map((event) => StudentModel.fromMap(map: event)).toList();

        return child;
      },
    );
  }

  Widget events(Widget child) {
    return Query(
      options: QueryOptions(
        document: gql(queryEvents),
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        List raw = result.data!['events'];

        if (raw == Null || raw.isEmpty) {
          return child;
        }

        _events = raw.map((event) => EventModel.fromMap(map: event)).toList();

        return child;
      },
    );
  }

  Widget studentById({required Widget child, required int studentId}) {
    return Query(
      options: QueryOptions(
        document: gql(queryStudentById),
        variables: {'id': studentId},
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        Map raw = result.data!['studentById'];
        // print('p  $raw');
        if (raw == Null || raw.isEmpty) {
          return child;
        }

        _student = StudentModel.fromMap(map: raw);

        return child;
      },
    );
  }

  Widget studentByToken({required Widget child}) {
    return Query(
      options: QueryOptions(
        document: gql(queryStudentByToken),
        variables: {'token': _myBox.get('token')},
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        Map raw = result.data!['studentByToken'];
        // print('p  $raw');
        if (raw == Null || raw.isEmpty) {
          return SizedBox(
            height: 100,
            child: Container(
              color: Colors.red,
              child: Text('Please Try Again'),
            ),
          );
          ;
        }

        _student = StudentModel.fromMap(map: raw);

        return child;
      },
    );
  }

  Widget eventsByStudent({required Widget child, required int studentId}) {
    return Query(
      options: QueryOptions(
        document: gql(queryEventsByStudent),
        variables: {'id': studentId},
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          return Text(result.exception.toString());
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        List raw = result.data!['eventsByStudent'];

        if (raw == Null || raw.isEmpty) {
          return child;
        }

        _studentEvents =
            raw.map((event) => EventModel.fromMap(map: event)).toList();

        return child;
      },
    );
  }

  final String queryStudents = """
    query {
      students{
        studentId
        lastName
        firstName
        bonusPoints {
          points
        }
        rank
        graduationYear
        eventsAttended
        email
        qrcode
        points
        prizes{
          id
          name
          description
          image
        }
      }
    }
""";

  final String queryStudentById = """
  query studentById(\$id: Int) {
    studentById(studentId: \$id){
      studentId
      lastName
      firstName
      bonusPoints {
      	points
      }
      rank
      graduationYear
      eventsAttended
      email
      qrcode
      points
      prizes{
          id
          name
          description
          image
        }
    }
  }
""";

  final String queryStudentByToken = """
    query sutdentByToken(\$token:String){
      studentByToken(token: \$token){
        studentId
        lastName
        firstName
        bonusPoints {
          points
        }
        rank
        graduationYear
        eventsAttended
        email
        qrcode
        points
        prizes{
          id
          name
          description
          image
        }
      }
    }
""";

  final String queryEventsByStudent = """
    query eventsByStudent(\$id: Int) {
      eventsByStudent(studentId: \$id){
        id
        name
        location
        startTime
        endTime
        points
        elapsed
        isRecurring
        relapseTime
        totalCount
        pastCount
        checkinOpen
      }
    }
  """;

  final String queryEvents = """
    query{
      events{
        id
        name
        location
        startTime
        endTime
        points
        elapsed
        isRecurring
        relapseTime
        totalCount
        pastCount
        checkinOpen
      }
    }
  """;

  @override
  Widget build(BuildContext context) {
    print(_myBox.get('token'));

    ThemeModel _theme = _themeController.getTheme(_themeBox.get('token'));

    final List<Widget> widgetOptions = <Widget>[
      newLeaderboard(
        student: _student,
        students: _students,
        theme: _theme,
      ),
      newProfile(
        student: _student,
        events: _studentEvents,
        theme: _theme,
      ),
      Calendar(
        events: _events,
        theme: _theme,
      ),
    ];

    String adjustName(String fullName) {
      List name_splitted = fullName.split(' ');

      String name_adjusted = name_splitted[0].toString() +
          " " +
          name_splitted[name_splitted.length - 1].toString()[0] +
          ".";

      return (name_adjusted);
    }

    return Query(
      options: QueryOptions(
        document: gql(queryStudentByToken),
        variables: {'token': _myBox.get('token')},
        pollInterval: const Duration(seconds: 10),
      ),
      builder: (QueryResult result,
          {VoidCallback? refetch, FetchMore? fetchMore}) {
        if (result.hasException) {
          // print(_myBox.get('token'));
          // print(result.exception.toString());
          // _myBox.put('token', 'none');
          // return Text(result.exception.toString());

          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => ValidationPage()),
          );
        }

        if (result.isLoading) {
          return const CircularProgressIndicator();
        }

        Map raw = result.data!['studentByToken'];
        // print('p  $raw');
        if (raw == Null || raw.isEmpty) {
          return SizedBox(
            height: 100,
            child: Container(
              color: Colors.red,
              child: Text('Please Try Again'),
            ),
          );
          ;
        }

        _student = StudentModel.fromMap(map: raw);

        return Scaffold(
          appBar: AppBar(
            shadowColor: _theme.secondary,
            backgroundColor: _theme.primary,
            foregroundColor: _theme.text,
            title: Text(
              adjustName('${_student.firstName} ${_student.lastName}'),
              textScaleFactor: 1.7,
              style: GoogleFonts.notoSans(fontWeight: FontWeight.w500),
            ),
            actions: <Widget>[
              IconButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => helpPage()),
                  );
                },
                icon: Icon(Icons.help),
              ),
              // IconButton(
              //   icon: const Icon(Icons.qr_code_2),
              //   onPressed: () {
              //     showDialog(
              //       context: context,
              //       builder: (BuildContext context) => AlertDialog(
              //         content: SizedBox(
              //           width: 500,
              //           height: 260,
              //           child: QrImage(
              //             data: _student.studentId.toString(),
              //             version: QrVersions.auto,
              //             size: 100,
              //           ),
              //         ),
              //       ),
              //     );
              //   },
              // ),
              IconButton(
                icon: const Icon(Icons.card_giftcard),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => PrizePage(
                              student: _student,
                            )),
                  );
                },
              ),
              IconButton(
                icon: const Icon(Icons.settings),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => SettingsPage()),
                  ).then(
                    (value) => setState(
                      () {
                        ThemeModel _theme =
                            _themeController.getTheme(_themeBox.get('token'));
                      },
                    ),
                  );
                },
              ),
            ],
          ),
          body: SafeArea(
            child: Center(
              child: events(
                students(
                  studentById(
                    studentId: _student.studentId,
                    child: eventsByStudent(
                      studentId: _student.studentId,
                      child: widgetOptions.elementAt(_selectedIndex),
                    ),
                  ),
                ),
              ),
            ),
          ),
          bottomNavigationBar: BottomNavigationBar(
            items: const <BottomNavigationBarItem>[
              BottomNavigationBarItem(
                icon: Icon(Icons.bar_chart_rounded),
                label: 'Leaderboard',
              ),
              BottomNavigationBarItem(
                icon: Icon(Icons.account_circle_outlined),
                label: 'Profile',
              ),
              BottomNavigationBarItem(
                icon: Icon(Icons.calendar_month),
                label: 'Calendar',
              ),
            ],
            selectedLabelStyle: GoogleFonts.notoSans(),
            unselectedLabelStyle: GoogleFonts.notoSans(),
            currentIndex: _selectedIndex,
            backgroundColor: _theme.primary,
            selectedItemColor: _theme.icon,
            unselectedItemColor: _theme.secondary,
            onTap: _onItemTapped,
          ),
        );
      },
    );
  }
}
