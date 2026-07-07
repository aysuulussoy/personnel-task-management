import 'package:flutter/material.dart';

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
  final TaskService _taskService = TaskService();

  late Future<List<TaskModel>> _tasksFuture;
  bool _isUpdating = false;

  @override
  void initState() {
    super.initState();
    _tasksFuture = _taskService.getMyTasks();
  }

  Future<void> _logout(BuildContext context) async {
    await AuthService().logout();

    if (!context.mounted) return;

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (_) => const LoginScreen(),
      ),
    );
  }

  Future<void> _refreshTasks() async {
    setState(() {
      _tasksFuture = _taskService.getMyTasks();
    });
  }

  Future<void> _markAsCompleted(TaskModel task) async {
    setState(() {
      _isUpdating = true;
    });

    try {
      await _taskService.updateTaskStatus(task.id, 'COMPLETED');
      await _refreshTasks();

      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Task marked as completed.'),
        ),
      );
    } catch (error) {
      if (!mounted) return;

      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Task could not be updated.'),
        ),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isUpdating = false;
        });
      }
    }
  }

  Widget _buildStatusChip(String status) {
    return Chip(
      label: Text(status),
    );
  }

  Widget _buildTaskCard(TaskModel task) {
    final bool isCompleted = task.status.toUpperCase() == 'COMPLETED';

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
                fontSize: 17,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              task.description,
              style: const TextStyle(fontSize: 14),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                const Text(
                  'Status: ',
                  style: TextStyle(fontWeight: FontWeight.w600),
                ),
                _buildStatusChip(task.status),
              ],
            ),
            if (task.assignedByManagerName != null) ...[
              const SizedBox(height: 6),
              Text(
                'Assigned by: ${task.assignedByManagerName}',
                style: const TextStyle(fontSize: 13),
              ),
            ],
            if (!isCompleted) ...[
              const SizedBox(height: 12),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: _isUpdating ? null : () => _markAsCompleted(task),
                  child: _isUpdating
                      ? const Text('Updating...')
                      : const Text('Mark as Completed'),
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildTaskList(List<TaskModel> tasks) {
    if (tasks.isEmpty) {
      return const Center(
        child: Text(
          'No tasks assigned yet.',
          style: TextStyle(fontSize: 16),
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _refreshTasks,
      child: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          const Text(
            'Welcome, Employee!',
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '${tasks.length} task(s) assigned to you',
            style: const TextStyle(fontSize: 14),
          ),
          const SizedBox(height: 16),
          ...tasks.map(_buildTaskCard),
        ],
      ),
    );
  }

  Widget _buildErrorState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Could not load tasks.',
              style: TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: _refreshTasks,
              child: const Text('Try Again'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Tasks'),
        actions: [
          IconButton(
            onPressed: _refreshTasks,
            icon: const Icon(Icons.refresh),
          ),
          IconButton(
            onPressed: () => _logout(context),
            icon: const Icon(Icons.logout),
          ),
        ],
      ),
      body: FutureBuilder<List<TaskModel>>(
        future: _tasksFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }

          if (snapshot.hasError) {
            return _buildErrorState();
          }

          final tasks = snapshot.data ?? [];

          return _buildTaskList(tasks);
        },
      ),
    );
  }
}