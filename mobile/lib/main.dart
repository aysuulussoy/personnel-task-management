import 'package:flutter/material.dart';

import 'screens/login_screen.dart';

void main() {
  runApp(const PersonnelTaskManagementApp());
}

class PersonnelTaskManagementApp extends StatelessWidget {
  const PersonnelTaskManagementApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Personnel Task Management',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const LoginScreen(),
    );
  }
}