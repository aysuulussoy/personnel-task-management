# Personnel Task Management

Personnel Task Management is an internship project developed with a Spring Boot backend, PostgreSQL database, and Flutter mobile application.

The project is designed for an internal company-style task management flow. Managers can assign tasks to employees, employees can view only their own assigned tasks, and task statuses can be tracked from both the backend API and the mobile application.

## Technologies Used

### Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* PostgreSQL
* Lombok
* Maven
* Docker
* Docker Compose
* Swagger / OpenAPI
* JUnit 5
* MockMvc
* Postman

### Mobile

* Flutter
* Dart
* Android Emulator
* Flutter Secure Storage
* HTTP package

## Main Features

* Employee management
* Manager and employee role structure
* Database-based login and register system
* JWT-based authentication
* Current user information with `/api/auth/me`
* Manager-only user registration
* Soft delete for employees
* Task assignment by managers
* Employee-specific task visibility
* Task status tracking
* Completed task date tracking
* DTO-based request and response structure
* Validation for request data
* Global exception handling
* Report summary endpoint
* Completed task CSV export endpoint
* Swagger API documentation
* Docker Compose support for Spring Boot and PostgreSQL
* Flutter mobile application
* Personalized manager dashboard
* Personalized employee task screen
* Backend integration tests

## Project Structure

The backend follows a layered structure:

```text
controller   -> Handles API requests
service      -> Contains business logic
repository   -> Handles database access
entity       -> Represents database tables
dto          -> Defines request and response models
exception    -> Handles API errors in a structured way
security     -> Handles JWT token generation and filtering
util         -> Contains helper classes
```

The Flutter mobile app is located under the `mobile` folder:

```text
mobile/lib/core      -> API configuration
mobile/lib/models    -> Dart models for API responses
mobile/lib/services  -> API service classes
mobile/lib/screens   -> Mobile UI screens
```

## Running the Backend Locally

Before running the project locally, PostgreSQL should be installed and a database named `taskmanagement` should be created.

Example local database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

Then the project can be started from IntelliJ IDEA or with Maven:

```bash
mvn spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

Swagger UI can be accessed from:

```text
http://localhost:8080/swagger-ui/index.html
```

## Running the Mobile App

The Flutter mobile application is located under the `mobile` folder.

Before running the mobile app, make sure the Spring Boot backend is running on port `8080`.

For Android emulator testing, the app connects to the backend using:

```text
http://10.0.2.2:8080/api
```

Run the mobile app:

```bash
cd mobile
flutter pub get
flutter run
```

## Running with Docker

The project can also be run with Docker Compose. This starts both the Spring Boot application and PostgreSQL database as containers.

Run the following command in the project root directory:

```bash
docker compose up --build
```

After the containers start, the API can be tested from:

```text
http://localhost:8080
```

To stop the containers:

```bash
docker compose down
```

## Security

The project uses JWT-based authentication.

Users are stored in the PostgreSQL `employees` table. Passwords are not stored as plain text. They are saved as MD5 hash values in this internship version.

An initial manager user is created automatically when the application starts:

```text
username: manager
password: manager123
role: MANAGER
```

Authentication flow:

1. The user sends a login request to `/api/auth/login`.
2. If the username and password are correct, the system returns a JWT token.
3. Protected endpoints are accessed using the JWT token as a Bearer Token.
4. The mobile app calls `/api/auth/me` to get the logged-in user's information.
5. The user is redirected according to their role.

Example login request:

```http
POST /api/auth/login
```

```json
{
  "username": "manager",
  "password": "manager123"
}
```

Example protected request header:

```text
Authorization: Bearer <jwt_token>
```

Access rules:

```text
POST /api/auth/login                 -> public
GET  /api/auth/me                    -> authenticated users
POST /api/auth/register              -> MANAGER only
/api/employees/**                    -> MANAGER only
/api/reports/**                      -> MANAGER only
POST /api/tasks/assign               -> MANAGER only
GET  /api/tasks                      -> MANAGER only
GET  /api/tasks/my                   -> authenticated users
PATCH /api/tasks/{id}/status         -> authenticated users with task permission
```

## Swagger / OpenAPI Documentation

Swagger UI was added to make the API endpoints easier to view and test.

After running the application, Swagger UI can be accessed from:

```text
http://localhost:8080/swagger-ui/index.html
```

To test protected endpoints in Swagger UI:

1. Login from `/api/auth/login` and copy the returned token.
2. Click the `Authorize` button in Swagger UI.
3. Paste the JWT token into the authorization field.
4. Test protected endpoints with the authorized token.

## API Endpoints

### Authentication Endpoints

| Method | Endpoint             | Description                         |
| ------ | -------------------- | ----------------------------------- |
| POST   | `/api/auth/login`    | Login and receive a JWT token       |
| GET    | `/api/auth/me`       | Get logged-in user information      |
| POST   | `/api/auth/register` | Register a new user by manager      |

Example login response:

```json
{
  "token": "jwt_token_here",
  "userId": 5,
  "username": "manager",
  "role": "MANAGER"
}
```

Example `/api/auth/me` response:

```json
{
  "id": 5,
  "username": "manager",
  "fullName": "Aysu Manager",
  "email": "manager@yurticikargo.com",
  "role": "MANAGER",
  "active": true
}
```

Example register request:

```json
{
  "fullName": "Delivery Employee",
  "email": "delivery1@yurticikargo.com",
  "username": "delivery1",
  "password": "delivery123",
  "role": "EMPLOYEE"
}
```

### Employee Endpoints

| Method | Endpoint                | Description           |
| ------ | ----------------------- | --------------------- |
| POST   | `/api/employees`        | Create a new employee |
| GET    | `/api/employees`        | List employees        |
| GET    | `/api/employees/active` | List active employees |
| GET    | `/api/employees/{id}`   | Get employee by id    |
| PUT    | `/api/employees/{id}`   | Update employee       |
| DELETE | `/api/employees/{id}`   | Soft delete employee  |

### Task Endpoints

| Method | Endpoint                            | Description                              |
| ------ | ----------------------------------- | ---------------------------------------- |
| POST   | `/api/tasks/assign?employeeId={id}` | Assign a task to an employee             |
| GET    | `/api/tasks`                        | List all tasks for manager               |
| GET    | `/api/tasks/my`                     | List logged-in employee's own tasks      |
| PATCH  | `/api/tasks/{id}/status`            | Update task status                       |

Example task assignment request:

```json
{
  "title": "Delivery Check",
  "description": "Check delivery list."
}
```

### Report Endpoints

| Method | Endpoint                              | Description                   |
| ------ | ------------------------------------- | ----------------------------- |
| GET    | `/api/reports/summary`                | Get employee and task summary |
| GET    | `/api/reports/completed-tasks/export` | Export completed tasks as CSV |

Example completed task export request:

```text
GET /api/reports/completed-tasks/export?startDate=2026-06-01T00:00:00&endDate=2026-06-30T23:59:59
```

## Mobile Application

A Flutter mobile application was added under the `mobile` folder.

The mobile app uses one common login screen for both managers and employees. After login, the app checks the user's role and redirects the user to the correct screen.

Manager users can:

* See their own name on the dashboard
* View employee count, active task count, and completed task count
* Select an employee from a dropdown
* Assign tasks to employees
* Track all task statuses

Employee users can:

* See their own name on the task screen
* View only their own assigned tasks
* Mark assigned tasks as completed

The mobile app uses `/api/auth/me` to get the logged-in user's information and show personalized screens.

## Demo Users

| Username      | Password     | Role     | Full Name          |
| ------------- | ------------ | -------- | ------------------ |
| manager       | manager123   | MANAGER  | Aysu Manager       |
| branchmanager | branch123    | MANAGER  | Branch Manager     |
| delivery1     | delivery123  | EMPLOYEE | Delivery Employee  |
| warehouse1    | warehouse123 | EMPLOYEE | Warehouse Employee |
| support1      | support123   | EMPLOYEE | Support Employee   |

New users can be created by a manager through the backend register endpoint. A mobile register screen was not added because this project is designed as an internal company-style application. User creation is handled by manager authorization from the backend side instead of allowing open mobile registration.

## Final Demo Flow

The final tested demo flow is:

```text
1. Login as manager.
2. Assign "Delivery Check" to Delivery Employee.
3. Login as delivery1.
4. Delivery Employee sees the assigned task.
5. Login as warehouse1.
6. Warehouse Employee does not see Delivery Employee's task.
7. Delivery Employee marks the task as completed.
8. Manager refreshes the dashboard and sees the task as COMPLETED.
```

This flow verifies role-based access, employee-specific task visibility, task completion, and manager-side task tracking.

## Backend Tests

The project includes backend integration tests with JUnit 5 and MockMvc.

Tested cases include:

```text
- Spring context loading
- Manager login and JWT token response
- Manager loading employee list
- Manager assigning task
- Manager loading all tasks
- Employee seeing only own tasks
- Assigned employee completing own task
```

Current test result:

```text
7 tests passed
```

## Error Handling

The project uses a global exception handling structure. API errors are returned in a standard JSON format.

Example error response:

```json
{
  "timestamp": "2026-07-10T12:55:42.144+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Authenticated user does not have permission",
  "path": "/api/tasks",
  "validationErrors": null
}
```

Other tested responses:

```text
200 OK           -> Successful request
400 Bad Request  -> Invalid request or duplicate username/email
401 Unauthorized -> Missing or invalid authentication
403 Forbidden    -> Authenticated user does not have permission
404 Not Found    -> Requested resource does not exist
```

## Docker Support

Docker support was added with the following files:

```text
Dockerfile
.dockerignore
docker-compose.yml
```

The `Dockerfile` builds and runs the Spring Boot application.
The `docker-compose.yml` file runs the Spring Boot application and PostgreSQL database together.

## Possible Improvements

* Add a manager-only mobile user creation screen
* Add task priority levels
* Add task deadlines
* Add push notifications for new task assignments
* Add reporting charts to the manager dashboard
* Add Flutter service layer tests
* Add refresh token support
* Replace MD5 with a stronger password hashing algorithm in a production-level version
* Deploy the backend to a real server with HTTPS