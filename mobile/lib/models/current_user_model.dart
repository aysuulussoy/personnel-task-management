class CurrentUserModel {
  final int id;
  final String username;
  final String fullName;
  final String email;
  final String role;
  final bool active;

  CurrentUserModel({
    required this.id,
    required this.username,
    required this.fullName,
    required this.email,
    required this.role,
    required this.active,
  });

  factory CurrentUserModel.fromJson(Map<String, dynamic> json) {
    return CurrentUserModel(
      id: json['id'],
      username: json['username'],
      fullName: json['fullName'],
      email: json['email'],
      role: json['role'],
      active: json['active'] ?? true,
    );
  }
}