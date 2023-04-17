import 'dart:io';

import 'package:cache_manager/core/write_cache_service.dart';
import 'package:email_validator/email_validator.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:flutter/src/widgets/placeholder.dart';
import 'package:flutter_application_1/AvailableStudentModel.dart';
import 'package:flutter_application_1/pages/LogInPage.dart';
import 'package:flutter_application_1/pages/ValidationPage.dart';
import 'package:graphql_flutter/graphql_flutter.dart';
import 'package:hive/hive.dart';

class CreateAccount extends StatefulWidget {
  const CreateAccount({super.key, required this.availableStudent});

  final AvailableStudentModel availableStudent;

  @override
  State<CreateAccount> createState() =>
      _CreateAccountState(availableStudent: availableStudent);
}

class _CreateAccountState extends State<CreateAccount> {
  _CreateAccountState({required this.availableStudent});
  final AvailableStudentModel availableStudent;

  final _myBox = Hive.box('token');

  final String mutationCreateStudent = """
    mutation createStudent(\$email:String!,\$password:String!,\$id:Int!){
      createStudent(email:\$email,password:\$password,studentId:\$id){
        token
      }
    }
""";

  final emailController = TextEditingController();

  final passwordController = TextEditingController();

  @override
  void dispose() {
    emailController.dispose();
    passwordController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final _formKey = GlobalKey<FormState>();
    RegExp regex = RegExp(r'^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$');
    return Form(
      key: _formKey,
      child: Scaffold(
        appBar: AppBar(title: Text("Create Account")),
        body: SingleChildScrollView(
          physics: BouncingScrollPhysics(),
          child: Column(
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
                          validator: (value) {
                            print(EmailValidator.validate(
                                value.toString(), true));
                            print(value);
                            if (value == null ||
                                value.isEmpty ||
                                !EmailValidator.validate(
                                    value.toString(), true)) {
                              return 'Please enter a valid email';
                            }
                            return null;
                          },
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Enter your Email',
                          ),
                        ),
                      ),
                      Padding(
                        padding: EdgeInsets.all(5),
                        child: TextFormField(
                          controller: passwordController,
                          obscureText: true,
                          validator: (value) {
                            if (value == null ||
                                value.isEmpty ||
                                !regex.hasMatch(value)) {
                              return 'Include 8+ characters, Capital letter, and a #';
                            }
                            return null;
                          },
                          decoration: const InputDecoration(
                            border: UnderlineInputBorder(),
                            labelText: 'Enter your Password',
                          ),
                        ),
                      ),
                      // TextButton(
                      //   onPressed: () {
                      //     Navigator.push(
                      //       context,
                      //       MaterialPageRoute(
                      //         builder: (context) => CreateAccount(
                      //           availableStudent: availableStudent,
                      //         ),
                      //       ),
                      //     );
                      //   },
                      //   child: Text("Create Account"),
                      // )
                      Mutation(
                        options: MutationOptions(
                          document: gql(mutationCreateStudent),
                          update: (cache, result) => cache,
                          onCompleted: (dynamic resultData) {
                            print(resultData);
                            Map raw = resultData['createStudent'];
                            // print(resultData);

                            String token = raw['token'];

                            _myBox.put('token', token);

                            Navigator.pushAndRemoveUntil(
                              context,
                              MaterialPageRoute(
                                builder: (context) => ValidationPage(),
                              ),
                              (Route<dynamic> route) => false,
                            );
                          },
                        ),
                        builder: (runMutation, result) {
                          return TextButton(
                            onPressed: () {
                              if (_formKey.currentState!.validate()) {
                                // If the form is valid, display a snackbar. In the real world,
                                // you'd often call a server or save the information in a database.
                                // print('hi');

                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(
                                      content: Text('Validating...')),
                                );
                                runMutation({
                                  "id": availableStudent.studentId,
                                  "email": emailController.text,
                                  "password": passwordController.text
                                });
                              }
                            },
                            child: Text('Create Account'),
                          );
                        },
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
