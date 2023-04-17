import 'dart:io';
import 'package:cache_manager/core/cache_manager_utils.dart';
import 'package:email_validator/email_validator.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/AvailableStudentModel.dart';
import 'package:flutter_application_1/pages/CreateAccountPage.dart';
import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:hive/hive.dart';

class LogInPage extends StatefulWidget {
  const LogInPage({super.key});

  @override
  State<LogInPage> createState() => _LogInPageState();
}

class _LogInPageState extends State<LogInPage> {
  final emailController = TextEditingController();

  final passwordController = TextEditingController();

  final availableStudentController = TextEditingController();

  final String queryAvailableStudentByID = """
      query availableStudentByStudentId(\$id:Int){
        availableStudentByStudentId(studentId:\$id){
          studentId
          lastName
          firstName
          graduationYear
        }
      }
""";

  final String mutationTokenAuth = """
    mutation tokenAuth(\$email:String!,\$password:String!){
      tokenAuth(username:\$email,password:\$password){
        token
        payload
      }
    }
""";

  @override
  void dispose() {
    // TODO: implement dispose
    emailController.dispose();
    passwordController.dispose();
    availableStudentController.dispose();
    super.dispose();
  }

  final _myBox = Hive.box('token');

  @override
  Widget build(BuildContext context) {
    final _formKey = GlobalKey<FormState>();
    RegExp regex = RegExp(r'^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$');
    bool valid = false;

    return Form(
      key: _formKey,
      child: Scaffold(
        body: SingleChildScrollView(
          physics: BouncingScrollPhysics(),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Row(
                children: [
                  Flexible(
                    child: Padding(
                      padding: EdgeInsets.only(
                          top: 60, left: 20, right: 20, bottom: 5),
                      child: Image.network(
                        'https://cdn.discordapp.com/attachments/1034701571155566642/1096550357938749481/ezgif.com-gif-maker_4.png',
                        fit: BoxFit.fill,
                      ),
                    ),
                  ),
                ],
              ),
              SizedBox(
                height: 400,
                child: FractionallySizedBox(
                  widthFactor: 0.7,
                  child: Column(
                    children: [
                      Padding(
                        padding: EdgeInsets.all(5),
                        child: TextFormField(
                          controller: emailController,
                          // validator: (value) {
                          //   if (value == null || value.isEmpty || valid) {
                          //     return 'Email';
                          //   }
                          //   return null;
                          // },
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Enter your email',
                          ),
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.all(5),
                        child: TextFormField(
                          controller: passwordController,
                          obscureText: true,
                          validator: (value) {
                            if (value == null || value.isEmpty || valid) {
                              return 'Invalid email or password';
                            }
                            return null;
                          },
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Enter your Password',
                          ),
                        ),
                      ),
                      Mutation(
                        options: MutationOptions(
                          document: gql(mutationTokenAuth),
                          update: (cache, result) => cache,
                          onError: (error) {
                            valid = true;
                            _formKey.currentState!.validate();
                            // if (_formKey.currentState!.validate()) {
                            //   // If the form is valid, display a snackbar. In the real world,
                            //   // you'd often call a server or save the information in a database.
                            //   ScaffoldMessenger.of(context).showSnackBar(
                            //     const SnackBar(content: Text('hehe Data')),
                            //   );
                            // }
                          },
                          onCompleted: (dynamic resultData) {
                            // print(resultData);
                            Map raw = resultData['tokenAuth'];

                            String token = raw['token'];

                            // print(token);
                            // print(
                            //     "email: ${emailController.text} password: ${passwordController.text}");

                            _myBox.put('token', token);

                            return Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (context) => ValidationPage()),
                            );
                          },
                        ),
                        builder: (runMutation, result) {
                          return TextButton(
                            onPressed: () {
                              valid = false;

                              if (_formKey.currentState!.validate()) {
                                // If the form is valid, display a snackbar. In the real world,
                                // you'd often call a server or save the information in a database.
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(
                                      content: Text('Validating...')),
                                );
                                runMutation({
                                  "email": emailController.text,
                                  "password": passwordController.text
                                });
                              }
                            },
                            child: Text('Log In'),
                          );
                        },
                      ),
                      TextButton(
                        onPressed: () {
                          // Navigator.push(
                          //   context,
                          //   MaterialPageRoute(
                          //     builder: (context) => CreateAccount(),
                          //   ),
                          // );

                          showDialog(
                            context: context,
                            builder: (BuildContext context) => AlertDialog(
                              content: SizedBox(
                                child: TextFormField(
                                  controller: availableStudentController,
                                  decoration: const InputDecoration(
                                    border: UnderlineInputBorder(),
                                    labelText: 'Enter your Student Id',
                                  ),
                                ),
                              ),
                              actions: <Widget>[
                                TextButton(
                                  onPressed: () {
                                    showDialog(
                                      context: context,
                                      builder: (BuildContext context) =>
                                          AlertDialog(
                                        content: Query(
                                          options: QueryOptions(
                                            document:
                                                gql(queryAvailableStudentByID),
                                            variables: {
                                              'id': int.parse(
                                                  availableStudentController
                                                      .text),
                                            },
                                            pollInterval:
                                                const Duration(seconds: 10),
                                          ),
                                          builder: (QueryResult result,
                                              {VoidCallback? refetch,
                                              FetchMore? fetchMore}) {
                                            if (result.hasException) {
                                              return Text(
                                                  result.exception.toString());
                                            }

                                            if (result.isLoading) {
                                              return const CircularProgressIndicator();
                                            }

                                            Map raw = result.data![
                                                'availableStudentByStudentId'];
                                            // print('p  $raw');
                                            if (raw == Null || raw.isEmpty) {
                                              return SizedBox(
                                                height: 100,
                                                child: Container(
                                                  color: Colors.red,
                                                  child:
                                                      Text('Please Try Again'),
                                                ),
                                              );
                                            }

                                            AvailableStudentModel
                                                _availableStudent =
                                                AvailableStudentModel.fromMap(
                                                    map: raw);

                                            return Container(
                                              child: Column(children: [
                                                Text('Is this you?'),
                                                Text(_availableStudent
                                                    .firstName),
                                                Text(
                                                    _availableStudent.lastName),
                                                TextButton(
                                                    onPressed: () {
                                                      Navigator.push(
                                                        context,
                                                        MaterialPageRoute(
                                                          builder: (context) =>
                                                              CreateAccount(
                                                            availableStudent:
                                                                _availableStudent,
                                                          ),
                                                        ),
                                                      );
                                                    },
                                                    child: Text('Confirm'))
                                              ]),
                                            );
                                          },
                                          // ),
                                        ),
                                      ),
                                    );
                                  },
                                  child: Text('check id'),
                                )
                              ],
                            ),
                          );
                        },
                        child: Text("Create Account"),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
