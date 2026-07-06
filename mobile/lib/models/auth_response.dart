class AuthResponse {
  final String token;
  final int userId;
  final String username;
  final String role;

  AuthResponse({
    required this.token,
    required this.userId,
    required this.username,
    required this.role,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'],
      userId: json['userId'],
      username: json['username'],
      role: json['role'],
    );
  }
}