class EmployeeModel {
  final int id;
  final String username;
  final String fullName;
  final String email;
  final String role;
  final bool active;

  EmployeeModel({
    required this.id,
    required this.username,
    required this.fullName,
    required this.email,
    required this.role,
    required this.active,
  });

  factory EmployeeModel.fromJson(Map<String, dynamic> json) {
    return EmployeeModel(
      id: json['id'],
      username: json['username'] ?? '',
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      role: json['role'] ?? '',
      active: json['active'] ?? true,
    );
  }
}