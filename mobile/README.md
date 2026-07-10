# Personnel Task Management Mobile App

This is the Flutter mobile application for the Personnel Task Management project.

The app connects to the Spring Boot backend and provides role-based mobile screens for managers and employees.

## Main Features

* Common login screen for all users
* JWT token storage with Flutter Secure Storage
* Role-based navigation after login
* Current user information with `/api/auth/me`
* Personalized manager dashboard
* Personalized employee task screen
* Manager task assignment
* Employee-specific task visibility
* Task completion from the mobile app

## Tech Stack

* Flutter
* Dart
* Android Emulator
* HTTP package
* Flutter Secure Storage

## API Configuration

The API base URL is managed in:

```text
lib/core/api_config.dart
```

For Android emulator testing, the app uses:

```text
http://10.0.2.2:8080/api
```

For Flutter web or desktop testing, it uses:

```text
http://localhost:8080/api
```

## Running the App

Before running the mobile app, make sure the Spring Boot backend is running on port `8080`.

Then run:

```bash
flutter pub get
flutter run
```

If you are inside the project root folder, first go into the mobile folder:

```bash
cd mobile
flutter pub get
flutter run
```

## Demo Users

| Username      | Password     | Role     | Full Name          |
| ------------- | ------------ | -------- | ------------------ |
| manager       | manager123   | MANAGER  | Aysu Manager       |
| branchmanager | branch123    | MANAGER  | Branch Manager     |
| delivery1     | delivery123  | EMPLOYEE | Delivery Employee  |
| warehouse1    | warehouse123 | EMPLOYEE | Warehouse Employee |
| support1      | support123   | EMPLOYEE | Support Employee   |

## Mobile Flow

The mobile app uses one login screen for both roles.

After login:

* MANAGER users are redirected to the Manager Dashboard.
* EMPLOYEE users are redirected to the Employee Tasks screen.

Manager users can:

* See their own name on the dashboard
* View task summary cards
* Select an employee from the dropdown
* Assign a task to an employee
* View all tasks and their statuses

Employee users can:

* See their own name on the task screen
* View only their own assigned tasks
* Mark assigned tasks as completed

## Final Demo Flow

```text
1. Login as manager.
2. Assign a task to Delivery Employee.
3. Login as delivery1.
4. Delivery Employee sees the assigned task.
5. Login as warehouse1.
6. Warehouse Employee does not see Delivery Employee's task.
7. Delivery Employee marks the task as completed.
8. Login as manager again and check the completed status.
```

## Important Files

```text
lib/core/api_config.dart
lib/models/auth_response.dart
lib/models/current_user_model.dart
lib/models/employee_model.dart
lib/models/task_model.dart
lib/services/auth_service.dart
lib/services/employee_service.dart
lib/services/task_service.dart
lib/screens/login_screen.dart
lib/screens/manager_dashboard_screen.dart
lib/screens/employee_tasks_screen.dart
```

## Notes

The mobile app does not include a public register screen. User registration is handled from the backend by a manager through the register endpoint.

This is intentional because the project is designed as an internal company-style task management system. The mobile app focuses on login, task assignment, task tracking, and employee-specific task visibility.