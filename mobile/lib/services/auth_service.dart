import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../core/api_config.dart';
import '../models/auth_response.dart';
import '../models/current_user_model.dart';

class AuthService {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  Future<AuthResponse> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final data = AuthResponse.fromJson(jsonDecode(response.body));

      await _storage.write(key: 'token', value: data.token);
      await _storage.write(key: 'role', value: data.role);
      await _storage.write(key: 'userId', value: data.userId.toString());
      await _storage.write(key: 'username', value: data.username);

      return data;
    } else {
      throw Exception('Login failed');
    }
  }

  Future<CurrentUserModel> getCurrentUser() async {
    final token = await _storage.read(key: 'token');

    if (token == null || token.isEmpty) {
      throw Exception('Token not found. Please login again.');
    }

    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/auth/me'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return CurrentUserModel.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to load current user.');
    }
  }

  Future<void> logout() async {
    await _storage.deleteAll();
  }

  Future<String?> getToken() async {
    return _storage.read(key: 'token');
  }

  Future<String?> getRole() async {
    return _storage.read(key: 'role');
  }
}