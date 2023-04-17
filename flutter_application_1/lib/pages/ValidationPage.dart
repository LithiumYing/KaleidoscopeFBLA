import 'package:flutter/material.dart';
import 'package:flutter_application_1/pages/LogInPage.dart';
import 'package:flutter_application_1/pages/MainScaffold.dart';
import 'package:hive/hive.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:graphql_flutter/graphql_flutter.dart';

class ValidationPage extends StatefulWidget {
  const ValidationPage({super.key});

  @override
  State<ValidationPage> createState() => _ValidationPageState();
}

class _ValidationPageState extends State<ValidationPage> {
  final String mutationTokenAuth = """
    mutation verifyToken(\$token:String){
      verifyToken(token:\$token){
        payload,
      }
    }
""";

  // late NavigatorState _navigator;

  // @override
  // void didChangeDependencies() {
  //   _navigator = Navigator.of(context);
  //   super.didChangeDependencies();
  // }

  // @override
  // void dispose() {
  //   _navigator.pushAndRemoveUntil(
  //       MaterialPageRoute(
  //         builder: (context) => MainScaffold(),
  //       ),
  //       (route) => false);
  //   _navigator.pushAndRemoveUntil(
  //       MaterialPageRoute(
  //         builder: (context) => LogInPage(),
  //       ),
  //       (route) => false);
  //   super.dispose();
  // }

  final _myBox = Hive.box('token');

  @override
  Widget build(BuildContext context) {
    print('${_myBox.get('token').toString()} s');
    bool hasRun = false;
    print(
        'djafioadsjfioadjsfioajsdfiojdsaoifjoidsajiosdanvijasdiofjnsdaivhdaosihfjoasdihffvisaupu');
    return Mutation(
      options: MutationOptions(
        document: gql(mutationTokenAuth),
        update: (cache, result) => cache,
        onError: (error) {
          // print(hasRun);
          // print(error);

          if (!hasRun) {
            hasRun = true;
            Navigator.pushAndRemoveUntil(
              context,
              MaterialPageRoute(
                builder: (context) => LogInPage(),
              ),
              (Route<dynamic> route) => false,
            );
          }
        },
        onCompleted: (dynamic resultData) {
          // print(hasRun);
          if (!hasRun) {
            // print('${_myBox.get('token').toString()} s');
            hasRun = true;
            if (resultData == null) {
              return Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(
                  builder: (context) => LogInPage(),
                ),
                (Route<dynamic> route) => false,
              );
            }
            print(resultData);
            // print(data);
            Map raw = resultData!['verifyToken']['payload'];
            // print(raw);
            String email = raw['username'];

            print(email);

            if (email == "") {
              print("case 1");
              return Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(
                  builder: (context) => LogInPage(),
                ),
                (Route<dynamic> route) => false,
              );
            } else {
              print("case 2");
              return Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(
                  builder: (context) => MainScaffold(),
                ),
                (Route<dynamic> route) => false,
              );
            }
          }
        },
      ),
      builder: (runMutation, result) {
        // print('${_myBox.get('token').toString()} j');
        runMutation(
          {
            "token": _myBox.get('token').toString(),
          },
        );
        return Scaffold(
          body: Center(
            child: SizedBox(
              child: CircularProgressIndicator(
                strokeWidth: 4,
              ),
              height: 200,
              width: 200,
            ),
          ),
        );
      },
    );
  }
}
