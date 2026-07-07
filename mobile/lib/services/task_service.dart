import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../core/api_config.dart';
import '../models/task_model.dart';

class TaskService {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  Future<List<TaskModel>> getMyTasks() async {
    final token = await _storage.read(key: 'token');

    if (token == null || token.isEmpty) {
      throw Exception('Token not found. Please login again.');
    }

    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/tasks/my'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);

      return data
          .map((item) => TaskModel.fromJson(item as Map<String, dynamic>))
          .toList();
    }

    throw Exception(
      'Failed to load tasks. Status: ${response.statusCode}, Body: ${response.body}',
    );
  }

  Future<TaskModel> updateTaskStatus(int taskId, String newStatus) async {
    final token = await _storage.read(key: 'token');

    if (token == null || token.isEmpty) {
      throw Exception('Token not found. Please login again.');
    }

    final response = await http.patch(
      Uri.parse('${ApiConfig.baseUrl}/tasks/$taskId/status?newStatus=$newStatus'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return TaskModel.fromJson(jsonDecode(response.body));
    }

    throw Exception(
      'Failed to update task status. Status: ${response.statusCode}, Body: ${response.body}',
    );
  }
}