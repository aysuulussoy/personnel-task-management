import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../core/api_config.dart';
import '../models/employee_model.dart';

class EmployeeService {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  Future<Map<String, String>> _authHeaders() async {
    final token = await _storage.read(key: 'token');

    if (token == null || token.isEmpty) {
      throw Exception('Token not found. Please login again.');
    }

    return {
      'Authorization': 'Bearer $token',
      'Content-Type': 'application/json',
    };
  }

  Future<List<EmployeeModel>> getEmployees() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/employees'),
      headers: await _authHeaders(),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map((item) => EmployeeModel.fromJson(item))
          .where((employee) => employee.active && employee.role == 'EMPLOYEE')
          .toList();
    }

    throw Exception('Failed to load employees. Status: ${response.statusCode}');
  }
}