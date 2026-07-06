import 'dart:convert';

import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;

import '../core/api_config.dart';
import '../models/auth_response.dart';

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

  print('Login status code: ${response.statusCode}');
  print('Login response body: ${response.body}');

  if (response.statusCode == 200) {
    final data = AuthResponse.fromJson(jsonDecode(response.body));

    await _storage.write(key: 'token', value: data.token);
    await _storage.write(key: 'role', value: data.role);
    await _storage.write(key: 'userId', value: data.userId.toString());
    await _storage.write(key: 'username', value: data.username);

    return data;
  } else {
    throw Exception(
      'Login failed with status ${response.statusCode}: ${response.body}',
    );
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