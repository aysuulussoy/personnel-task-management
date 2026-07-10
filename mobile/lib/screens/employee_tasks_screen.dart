import 'package:flutter/material.dart';

import '../models/current_user_model.dart';
import '../models/task_model.dart';
import '../services/auth_service.dart';
import '../services/task_service.dart';
import 'login_screen.dart';

class EmployeeTasksScreen extends StatefulWidget {
  const EmployeeTasksScreen({super.key});

  @override
  State<EmployeeTasksScreen> createState() => _EmployeeTasksScreenState();
}

class _EmployeeTasksScreenState extends State<EmployeeTasksScreen> {
  final AuthService _authService = AuthService();
  final TaskService _taskService = TaskService();

  CurrentUserModel? _currentUser;
  List<TaskModel> _tasks = [];

  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadEmployeeData();
  }

  Future<void> _loadEmployeeData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final currentUser = await _authService.getCurrentUser();
      final tasks = await _taskService.getMyTasks();

      if (!mounted) return;

      setState(() {
        _currentUser = currentUser;
        _tasks = tasks;
        _isLoading = false;
      });
    } catch (_) {
      if (!mounted) return;

      setState(() {
        _error = 'Failed to load employee data.';
        _isLoading = false;
      });
    }
  }

  Future<void> _markCompleted(TaskModel task) async {
    try {
      await _taskService.updateTaskStatus(task.id, 'COMPLETED');
      _showSnack('Task marked as completed.');
      await _loadEmployeeData();
    } catch (_) {
      _showSnack('Failed to update task.');
    }
  }

  Future<void> _logout() async {
    await _authService.logout();

    if (!mounted) return;

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (_) => const LoginScreen()),
    );
  }

  void _showSnack(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  String get _welcomeText {
    final fullName = _currentUser?.fullName;

    if (fullName == null || fullName.trim().isEmpty) {
      return 'Welcome!';
    }

    return 'Welcome, $fullName!';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Tasks'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadEmployeeData,
            tooltip: 'Refresh',
          ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
            tooltip: 'Logout',
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text(_error!))
              : RefreshIndicator(
                  onRefresh: _loadEmployeeData,
                  child: ListView(
                    padding: const EdgeInsets.all(16),
                    children: [
                      Text(
                        _welcomeText,
                        style: const TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        '${_tasks.length} task(s) assigned to you',
                        style: const TextStyle(fontSize: 14),
                      ),
                      const SizedBox(height: 20),
                      if (_tasks.isEmpty)
                        const Card(
                          child: Padding(
                            padding: EdgeInsets.all(16),
                            child: Text('No tasks assigned yet.'),
                          ),
                        )
                      else
                        ..._tasks.map(_buildTaskCard),
                    ],
                  ),
                ),
    );
  }

  Widget _buildTaskCard(TaskModel task) {
    final isCompleted = task.status == 'COMPLETED';

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              task.title,
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                fontSize: 15,
              ),
            ),
            const SizedBox(height: 6),
            Text(
              task.description,
              style: const TextStyle(fontSize: 13),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                const Text(
                  'Status: ',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                Chip(
                  label: Text(task.status),
                  backgroundColor:
                      isCompleted ? Colors.green.shade100 : Colors.blue.shade100,
                ),
              ],
            ),
            const SizedBox(height: 6),
            Text(
              'Assigned by: ${task.assignedByManagerName ?? '-'}',
              style: const TextStyle(fontSize: 12),
            ),
            if (!isCompleted) ...[
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => _markCompleted(task),
                  child: const Text('Mark as Completed'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }
}