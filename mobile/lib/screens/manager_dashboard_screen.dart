import 'package:flutter/material.dart';

import '../models/current_user_model.dart';
import '../models/employee_model.dart';
import '../models/task_model.dart';
import '../services/auth_service.dart';
import '../services/employee_service.dart';
import '../services/task_service.dart';
import 'login_screen.dart';

class ManagerDashboardScreen extends StatefulWidget {
  const ManagerDashboardScreen({super.key});

  @override
  State<ManagerDashboardScreen> createState() => _ManagerDashboardScreenState();
}

class _ManagerDashboardScreenState extends State<ManagerDashboardScreen> {
  final AuthService _authService = AuthService();
  final EmployeeService _employeeService = EmployeeService();
  final TaskService _taskService = TaskService();

  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _descriptionController = TextEditingController();

  CurrentUserModel? _currentUser;

  List<EmployeeModel> _employees = [];
  List<TaskModel> _tasks = [];

  EmployeeModel? _selectedEmployee;

  bool _isLoading = true;
  bool _isAssigning = false;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadDashboardData();
  }

  Future<void> _loadDashboardData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final currentUser = await _authService.getCurrentUser();
      final employees = await _employeeService.getEmployees();
      final tasks = await _taskService.getAllTasks();

      if (!mounted) return;

      setState(() {
        _currentUser = currentUser;
        _employees = employees;
        _tasks = tasks;
        _selectedEmployee = null;
        _isLoading = false;
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        _error = 'Failed to load manager dashboard data.';
        _isLoading = false;
      });
    }
  }

  Future<void> _assignTask() async {
    final title = _titleController.text.trim();
    final description = _descriptionController.text.trim();

    if (_selectedEmployee == null) {
      _showMessage('Please select an employee.');
      return;
    }

    if (title.isEmpty || description.isEmpty) {
      _showMessage('Please enter task title and description.');
      return;
    }

    setState(() {
      _isAssigning = true;
    });

    try {
      await _taskService.assignTask(
        employeeId: _selectedEmployee!.id,
        title: title,
        description: description,
      );

      _titleController.clear();
      _descriptionController.clear();

      if (!mounted) return;

      setState(() {
        _selectedEmployee = null;
      });

      _showMessage('Task assigned successfully.');
      await _loadDashboardData();
    } catch (e) {
      if (!mounted) return;
      _showMessage('Failed to assign task.');
    } finally {
      if (mounted) {
        setState(() {
          _isAssigning = false;
        });
      }
    }
  }

  Future<void> _logout() async {
    await _authService.logout();

    if (!mounted) return;

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (_) => const LoginScreen(),
      ),
    );
  }

  void _showMessage(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  String get _managerWelcomeText {
    final fullName = _currentUser?.fullName;

    if (fullName == null || fullName.trim().isEmpty) {
      return 'Welcome, Manager!';
    }

    return 'Welcome, $fullName!';
  }

  String _formatDate(String? value) {
    if (value == null || value.isEmpty) {
      return '-';
    }

    try {
      final date = DateTime.parse(value);
      final day = date.day.toString().padLeft(2, '0');
      final month = date.month.toString().padLeft(2, '0');
      final year = date.year.toString();
      return '$day.$month.$year';
    } catch (e) {
      return value;
    }
  }

  Color _statusColor(String status) {
    switch (status) {
      case 'COMPLETED':
        return Colors.green;
      case 'IN_PROGRESS':
        return Colors.orange;
      case 'CANCELLED':
        return Colors.red;
      default:
        return Colors.blue;
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Manager Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadDashboardData,
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
              ? _buildErrorState()
              : RefreshIndicator(
                  onRefresh: _loadDashboardData,
                  child: SingleChildScrollView(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildSummaryCards(),
                        const SizedBox(height: 18),
                        Text(
                          _managerWelcomeText,
                          style: const TextStyle(
                            fontSize: 21,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        const Text(
                          'You can assign tasks and follow employee task status here.',
                          style: TextStyle(fontSize: 13),
                        ),
                        const SizedBox(height: 20),
                        _buildAssignTaskCard(),
                        const SizedBox(height: 24),
                        _buildAllTasksSection(),
                      ],
                    ),
                  ),
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
            Text(
              _error!,
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 16),
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadDashboardData,
              child: const Text('Try Again'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSummaryCards() {
    final completedCount =
        _tasks.where((task) => task.status == 'COMPLETED').length;
    final activeCount = _tasks.length - completedCount;

    return Row(
      children: [
        Expanded(
          child: _buildSummaryCard(
            title: 'Employees',
            value: _employees.length.toString(),
            icon: Icons.people,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildSummaryCard(
            title: 'Active Tasks',
            value: activeCount.toString(),
            icon: Icons.assignment,
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: _buildSummaryCard(
            title: 'Completed',
            value: completedCount.toString(),
            icon: Icons.check_circle,
          ),
        ),
      ],
    );
  }

  Widget _buildSummaryCard({
    required String title,
    required String value,
    required IconData icon,
  }) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 8),
        child: Column(
          children: [
            Icon(icon, size: 22),
            const SizedBox(height: 8),
            Text(
              value,
              style: const TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              title,
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 11),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAssignTaskCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Assign New Task',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<EmployeeModel>(
              value: _selectedEmployee,
              decoration: const InputDecoration(
                labelText: 'Employee',
                border: OutlineInputBorder(),
              ),
              items: _employees
                  .map(
                    (employee) => DropdownMenuItem<EmployeeModel>(
                      value: employee,
                      child: Text(employee.fullName),
                    ),
                  )
                  .toList(),
              onChanged: (employee) {
                setState(() {
                  _selectedEmployee = employee;
                });
              },
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Task Title',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: _descriptionController,
              maxLines: 3,
              decoration: const InputDecoration(
                labelText: 'Task Description',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _isAssigning ? null : _assignTask,
                child: _isAssigning
                    ? const SizedBox(
                        height: 18,
                        width: 18,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('Assign Task'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAllTasksSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'All Tasks (${_tasks.length})',
          style: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 12),
        if (_tasks.isEmpty)
          const Card(
            child: Padding(
              padding: EdgeInsets.all(16),
              child: Text('No tasks found.'),
            ),
          )
        else
          ListView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            itemCount: _tasks.length,
            itemBuilder: (context, index) {
              final task = _tasks[index];
              return _buildTaskCard(task);
            },
          ),
      ],
    );
  }

  Widget _buildTaskCard(TaskModel task) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: Text(
                    task.title,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 15,
                    ),
                  ),
                ),
                Chip(
                  label: Text(
                    task.status,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 11,
                    ),
                  ),
                  backgroundColor: _statusColor(task.status),
                ),
              ],
            ),
            const SizedBox(height: 4),
            Text(task.description),
            const SizedBox(height: 10),
            Text(
              'Assigned to: ${task.assignedEmployeeName ?? "-"}',
              style: const TextStyle(fontSize: 12),
            ),
            Text(
              'Assigned by: ${task.assignedByManagerName ?? "-"}',
              style: const TextStyle(fontSize: 12),
            ),
            Text(
              'Created: ${_formatDate(task.createdAt)}',
              style: const TextStyle(fontSize: 12),
            ),
            if (task.completedAt != null)
              Text(
                'Completed: ${_formatDate(task.completedAt)}',
                style: const TextStyle(fontSize: 12),
              ),
          ],
        ),
      ),
    );
  }
}