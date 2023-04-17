class EventModel {
  final String? ID;
  final String name;
  final String location;
  final DateTime startTime;
  final DateTime endTime;
  final int points;
  final bool elapsed;
  // final bool isRecurring;
  // final BigInt relapseTime;
  // final int totalCount;
  // final int pastCount;
  final bool checkinOpen;

  EventModel({
    required this.ID,
    required this.name,
    required this.location,
    required this.startTime,
    required this.endTime,
    required this.points,
    required this.elapsed,
    // required this.isRecurring,
    // required this.relapseTime,
    // required this.totalCount,
    // required this.pastCount,
    required this.checkinOpen,
  });

  static EventModel fromMap({required Map map}) => EventModel(
        ID: map['ID'],
        name: map['name'],
        location: map['location'],
        startTime: DateTime.parse(map['startTime']),
        endTime: DateTime.parse(map['endTime']),
        points: map['points'],
        elapsed: map['elapsed'],
        // isRecurring: map['isRecurring'],
        // relapseTime: BigInt.from(map['relapseTime']),
        // totalCount: map['totalCount'],
        // pastCount: map['pastCount'],
        checkinOpen: map['checkinOpen'],
      );

  static EventModel empty() {
    return EventModel(
        ID: "",
        name: "",
        location: "",
        startTime: DateTime.now(),
        endTime: DateTime.now(),
        points: 0,
        elapsed: false,
        // isRecurring: false,
        // relapseTime: BigInt.zero, look more into this later maybe
        // totalCount: 0,
        // pastCount: 0,
        checkinOpen: false);
  }
}
