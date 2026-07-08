import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../core/api_config.dart';
import '../models/task_model.dart';

class TaskService {
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

  Future<List<TaskModel>> getMyTasks() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/tasks/my'),
      headers: await _authHeaders(),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((item) => TaskModel.fromJson(item)).toList();
    }

    throw Exception('Failed to load tasks. Status: ${response.statusCode}');
  }

  Future<List<TaskModel>> getAllTasks() async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/tasks'),
      headers: await _authHeaders(),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((item) => TaskModel.fromJson(item)).toList();
    }

    throw Exception('Failed to load all tasks. Status: ${response.statusCode}');
  }

  Future<TaskModel> assignTask({
    required int employeeId,
    required String title,
    required String description,
  }) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/tasks/assign?employeeId=$employeeId'),
      headers: await _authHeaders(),
      body: jsonEncode({
        'title': title,
        'description': description,
      }),
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      return TaskModel.fromJson(jsonDecode(response.body));
    }

    throw Exception('Failed to assign task. Status: ${response.statusCode}');
  }

  Future<TaskModel> updateTaskStatus(int taskId, String newStatus) async {
    final response = await http.patch(
      Uri.parse('${ApiConfig.baseUrl}/tasks/$taskId/status?newStatus=$newStatus'),
      headers: await _authHeaders(),
    );

    if (response.statusCode == 200) {
      return TaskModel.fromJson(jsonDecode(response.body));
    }

    throw Exception('Failed to update task status. Status: ${response.statusCode}');
  }
}