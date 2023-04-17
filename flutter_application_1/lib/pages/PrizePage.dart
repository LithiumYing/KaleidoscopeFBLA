import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/StudentModel.dart';
import 'package:graphql_flutter/graphql_flutter.dart';

class PrizePage extends StatelessWidget {
  PrizePage({super.key, required this.student});

  final StudentModel student;

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

    return Scaffold(
      appBar: AppBar(
        title: Text('Prizes'),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Center(
            child: Padding(
              padding: EdgeInsets.only(top: 20),
              child: Text(
                'Available Prizes',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 20),
              ),
            ),
          ),
          Divider(
            height: 2,
            color: Colors.black,
            indent: 50,
            endIndent: 50,
          ),
          SizedBox(
            height: 300,
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
                    height: 300,
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
                          title: Text(raw[index]['name'].toString()),
                          subtitle: Text(raw[index]['description'].toString()),
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
              padding: EdgeInsets.only(top: 20),
              child: Text(
                'Your Prizes',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 20),
              ),
            ),
          ),
          Divider(
            height: 2,
            color: Colors.black,
            indent: 50,
            endIndent: 50,
          ),
          SingleChildScrollView(
            child: SizedBox(
              height: 300,
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
                    title: Text(student.prizes[index]['name'].toString()),
                    subtitle:
                        Text(student.prizes[index]['description'].toString()),
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
