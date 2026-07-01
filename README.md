# Personnel Task Management

Personnel Task Management is a Spring Boot backend project developed during my internship. The project is designed to manage employees, assign tasks, update task statuses, and generate basic task reports.

The main goal of the project is to build a clean REST API using Spring Boot, PostgreSQL, JPA, DTOs, validation, exception handling, reporting, JWT authentication, and Docker support.

## Technologies Used

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
* Postman

## Main Features

* Employee management
* Manager and employee role structure
* Database-based login and register system
* JWT-based authentication
* Password hashing with MD5
* Initial manager user creation
* Manager-only user registration
* Soft delete for employees
* Task assignment by managers
* Task status tracking
* Completed task date tracking
* DTO-based request and response structure
* Validation for request data
* Global exception handling
* Report summary endpoint
* Completed task CSV export endpoint
* Docker Compose support for Spring Boot and PostgreSQL

## Project Structure

The project follows a layered backend structure:

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

## Running Locally

Before running the project locally, PostgreSQL should be installed and a database named `taskmanagement` should be created.

Example local database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

Then the project can be started from IntelliJ IDEA or with Maven.

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
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

Users are stored in the PostgreSQL `employees` table. Passwords are not stored as plain text. They are saved as MD5 hash values.

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
POST /api/auth/login          -> public
POST /api/auth/register       -> MANAGER only
/api/employees/**             -> MANAGER only
/api/reports/**               -> MANAGER only
POST /api/tasks/assign        -> MANAGER only
GET/PATCH/PUT /api/tasks/**   -> MANAGER or EMPLOYEE
```

## API Endpoints

### Authentication Endpoints

| Method | Endpoint             | Description                     |
| ------ | -------------------- | ------------------------------- |
| POST   | `/api/auth/login`    | Login and receive a JWT token   |
| POST   | `/api/auth/register` | Register a new user by manager  |

Example login request:

```json
{
  "username": "manager",
  "password": "manager123"
}
```

Example login response:

```json
{
  "token": "jwt_token_here",
  "userId": 5,
  "username": "manager",
  "role": "MANAGER"
}
```

Example register request:

```json
{
  "fullName": "JWT Test Employee",
  "email": "jwt.employee@yurticikargo.com",
  "username": "jwt.employee",
  "password": "employee123",
  "role": "EMPLOYEE"
}
```

Example register response:

```json
{
  "id": 6,
  "fullName": "JWT Test Employee",
  "email": "jwt.employee@yurticikargo.com",
  "username": "jwt.employee",
  "role": "EMPLOYEE",
  "active": true
}
```

### Employee Endpoints

| Method | Endpoint                | Description           |
| ------ | ----------------------- | --------------------- |
| POST   | `/api/employees`        | Create a new employee |
| GET    | `/api/employees`        | List all employees    |
| GET    | `/api/employees/active` | List active employees |
| GET    | `/api/employees/{id}`   | Get employee by id    |
| PUT    | `/api/employees/{id}`   | Update employee       |
| DELETE | `/api/employees/{id}`   | Soft delete employee  |

Example employee request:

```json
{
  "fullName": "Docker Test Employee",
  "email": "docker.employee@yurticikargo.com",
  "role": "EMPLOYEE"
}
```

### Task Endpoints

| Method | Endpoint                            | Description                  |
| ------ | ----------------------------------- | ---------------------------- |
| POST   | `/api/tasks/assign?employeeId={id}` | Assign a task to an employee |
| GET    | `/api/tasks`                        | List all tasks               |
| PATCH  | `/api/tasks/{id}/status`            | Update task status           |

Example task assignment request:

Headers:

```text
Authorization: Bearer <manager_jwt_token>
manager-id: 5
```

Body:

```json
{
  "title": "Docker task test",
  "description": "Testing task assignment while running the project with Docker Compose."
}
```

### Report Endpoints

| Method | Endpoint                              | Description                   |
| ------ | ------------------------------------- | ----------------------------- |
| GET    | `/api/reports/summary`                | Get employee and task summary |
| GET    | `/api/reports/completed-tasks/export` | Export completed tasks as CSV |

Example report summary response:

```json
{
  "totalEmployees": 7,
  "activeEmployees": 6,
  "inactiveEmployees": 1,
  "totalTasks": 4,
  "newTasks": 1,
  "inProgressTasks": 0,
  "completedTasks": 3,
  "canceledTasks": 0
}
```

Example completed task export request:

```text
GET /api/reports/completed-tasks/export?startDate=2026-06-01T00:00:00&endDate=2026-06-30T23:59:59
```

## Error Handling

The project uses a global exception handling structure. API errors are returned in a standard JSON format.

Example error response:

```json
{
  "timestamp": "2026-07-01T12:55:42.144+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Unauthorized",
  "path": "/api/auth/login",
  "validationErrors": null
}
```

Other tested responses:

```text
200 OK       -> Successful request
400 Bad Request -> Invalid request or duplicate username/email
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

## Future Improvements

* Add unit tests
* Add Swagger/OpenAPI documentation
* Add Postman collection
* Improve reporting options
* Add a simple frontend for demo purposes
* Replace MD5 with a stronger password hashing algorithm in a production-level version