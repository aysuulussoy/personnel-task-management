class TaskModel {
  final int id;
  final String title;
  final String description;
  final String status;
  final int? assignedEmployeeId;
  final String? assignedEmployeeName;
  final int? assignedByManagerId;
  final String? assignedByManagerName;
  final String? createdAt;
  final String? updatedAt;
  final String? completedAt;

  TaskModel({
    required this.id,
    required this.title,
    required this.description,
    required this.status,
    this.assignedEmployeeId,
    this.assignedEmployeeName,
    this.assignedByManagerId,
    this.assignedByManagerName,
    this.createdAt,
    this.updatedAt,
    this.completedAt,
  });

  factory TaskModel.fromJson(Map<String, dynamic> json) {
    return TaskModel(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      status: json['status'],
      assignedEmployeeId: json['assignedEmployeeId'],
      assignedEmployeeName: json['assignedEmployeeName'],
      assignedByManagerId: json['assignedByManagerId'],
      assignedByManagerName: json['assignedByManagerName'],
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
      completedAt: json['completedAt'],
    );
  }
}