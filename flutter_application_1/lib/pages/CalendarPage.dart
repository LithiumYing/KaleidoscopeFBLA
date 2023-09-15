import 'dart:io';

import 'package:date_format/date_format.dart';
import 'package:flutter/material.dart';
import 'package:flutter_application_1/EventModel.dart';
import 'package:flutter_application_1/themes/ThemeModel.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:table_calendar/table_calendar.dart';

class Calendar extends StatefulWidget {
  Calendar({super.key, required this.events, required this.theme});

  final List<EventModel> events;

  final ThemeModel theme;

  @override
  State<Calendar> createState() => _CalendarState(events: events, theme: theme);
}

class _CalendarState extends State<Calendar> {
  _CalendarState({required this.events, required this.theme});

  final List<EventModel> events;

  Map<DateTime, List<EventModel>> selectedEvents = {};

  // Map<DateTime, List<Event>>? selectedEvents;
  final ThemeModel theme;

  CalendarFormat _calendarFormat = CalendarFormat.month;
  DateTime _selectedDay = DateTime.utc(
      DateTime.now().year, DateTime.now().month, DateTime.now().day);
  DateTime _focusedDay = DateTime.utc(
      DateTime.now().year, DateTime.now().month, DateTime.now().day);
  DateTime schoolYearStart = DateTime.utc(2022, 9, 1);
  DateTime schoolYearEnd = DateTime.utc(2023, 9, 1);

  TextEditingController _eventController = TextEditingController();

  // @override
  // void initState() {
  //   // selectedEvents = {};
  //   super.initState();
  // }

  @override
  void dispose() {
    _eventController.dispose();
    super.dispose();
  }

  List<EventModel> _getEventFromDay(DateTime date) {
    // print(selectedEvents[date] ?? []);
    // print(date);
    return selectedEvents[date] ?? [];
  }

  @override
  Widget build(BuildContext context) {
    selectedEvents = {};
    for (EventModel event in events) {
      // print(event.name.toString());
      DateTimeRange dateRange = DateTimeRange(
          start: DateTime.utc(
              event.startTime.year, event.startTime.month, event.startTime.day),
          end: DateTime.utc(
              event.endTime.year, event.endTime.month, event.endTime.day));
      // print(dateRange);
      List<DateTime> days = [];
      for (var month = dateRange.start.month;
          month <= dateRange.end.month;
          month++) {
        for (var day = dateRange.start.day; day <= dateRange.end.day; day++) {
          days.add(DateTime.utc(event.startTime.year, month, day));
          // days.add(dateRange.start.add(Duration(days: day)));
        }
      }
      // print(days);
      for (DateTime day in days) {
        // print(day);

        if (!selectedEvents.containsKey(day)) {
          selectedEvents[day] = [];
        }
        selectedEvents[day]!.add(event);
      }
    }

    var adjustedHeight = MediaQuery.of(context).size.height -
        kToolbarHeight -
        MediaQuery.of(context).padding.top -
        kBottomNavigationBarHeight;
    // print(selectedEvents);

    return Container(
      color: theme.background,
      constraints: BoxConstraints.expand(),
      child: Column(
        children: [
          Expanded(
            child: SizedBox(
              height: adjustedHeight * 0.45,
              width: MediaQuery.of(context).size.width,
              child: TableCalendar(
                shouldFillViewport: true,
                headerStyle: HeaderStyle(
                  formatButtonVisible: false,
                  titleCentered: true,
                  titleTextStyle: TextStyle(color: theme.text),
                  formatButtonTextStyle: TextStyle(color: theme.text),
                ),
                daysOfWeekStyle: DaysOfWeekStyle(
                  weekdayStyle: TextStyle(color: theme.text),
                  weekendStyle: TextStyle(color: theme.text),
                ),
                focusedDay: _focusedDay,
                firstDay: schoolYearStart,
                lastDay: schoolYearEnd,
                calendarStyle: CalendarStyle(
                  defaultTextStyle: TextStyle(color: theme.text),
                  weekendTextStyle: TextStyle(color: theme.text2),
                  selectedTextStyle: TextStyle(color: theme.text),
                  selectedDecoration: BoxDecoration(
                    color: theme.primary,
                    shape: BoxShape.circle,
                  ),
                  todayDecoration: BoxDecoration(
                    color: theme.secondary,
                    shape: BoxShape.circle,
                  ),
                  markerDecoration: BoxDecoration(
                    color: theme.icon,
                    shape: BoxShape.circle,
                  ),
                ),
                selectedDayPredicate: (day) {
                  // Use `selectedDayPredicate` to determine which day is currently selected.
                  // If this returns true, then `day` will be marked as selected.

                  // Using `isSameDay` is recommended to disregard
                  // the time-part of compared DateTime objects.
                  return isSameDay(_selectedDay, day);
                },
                onDaySelected: (selectedDay, focusedDay) {
                  if (!isSameDay(_selectedDay, selectedDay)) {
                    // Call `setState()` when updating the selected day
                    setState(() {
                      _selectedDay = selectedDay;
                      _focusedDay = focusedDay;
                    });
                  }
                },
                onFormatChanged: (format) {
                  if (_calendarFormat != format) {
                    // Call `setState()` when updating calendar format
                    setState(() {
                      _calendarFormat = format;
                    });
                  }
                },
                onPageChanged: (focusedDay) {
                  // No need to call `setState()` here
                  _focusedDay = focusedDay;
                },
                eventLoader: _getEventFromDay,
              ),
            ),
          ),

          _getEventFromDay(_selectedDay).length == 0
              ? Container(
                  height: adjustedHeight * 0.48,
                  alignment: Alignment.center,
                  child: Container(
                    height: adjustedHeight * 0.2,
                    alignment: Alignment.center,
                    width: MediaQuery.of(context).size.width * 0.8,
                    decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(20),
                        color: theme.primary),
                    padding: EdgeInsets.all(10),
                    child: Text(
                      "No Events on this Date",
                      style: GoogleFonts.notoSans(
                        textStyle: TextStyle(fontSize: 20, color: theme.text),
                      ),
                    ),
                  ))
              : SingleChildScrollView(
                  child: SizedBox(
                    height: adjustedHeight * 0.48,
                    child: ListView(
                      children: [
                        ..._getEventFromDay(_selectedDay).map(
                          (EventModel event) => Padding(
                            padding: EdgeInsets.only(
                                right: 10, left: 10, top: 5, bottom: 5),
                            child: Container(
                              decoration: BoxDecoration(
                                  borderRadius: BorderRadius.circular(20),
                                  color: theme.primary),
                              child: ListTile(
                                textColor: theme.text,
                                tileColor: theme.primary,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(20)),
                                title: Text(
                                  event.name,
                                ),
                                subtitle: Text('${formatDate(event.startTime, [
                                      M,
                                      " ",
                                      dd,
                                      " ",
                                      HH,
                                      ":",
                                      nn
                                    ])} to ${formatDate(event.endTime, [
                                      M,
                                      " ",
                                      dd,
                                      " ",
                                      HH,
                                      ":",
                                      nn
                                    ])}'),
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

          // OutlinedButton(
          //   onPressed: () => showDialog(
          //     context: context,
          //     builder: (context) => AlertDialog(
          //       title: Text("Add Event"),
          //       content: TextFormField(
          //         controller: _eventController,
          //       ),
          //       actions: [
          //         TextButton(
          //           child: Text("Cancel"),
          //           onPressed: () => Navigator.pop(context),
          //         ),
          //         TextButton(
          //           child: Text("Ok"),
          //           onPressed: () {
          //             if (_eventController.text.isEmpty) {
          //             } else {
          //               if (selectedEvents[_selectedDay] != null) {
          //                 selectedEvents[_selectedDay]!.add(
          //                   Event(title: _eventController.text),
          //                 );
          //               } else {
          //                 selectedEvents[_selectedDay] = [
          //                   Event(title: _eventController.text)
          //                 ];
          //               }
          //             }
          //             Navigator.pop(context);
          //             _eventController.clear();
          //             setState(() {});
          //             return;
          //           },
          //         ),
          //       ],
          //     ),
          //   ),
          //   child: Text("Add Event"),
          // ),
        ],
      ),
    );
  }
}
