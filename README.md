# Personnel Task Management

Personnel Task Management is a Spring Boot backend project developed during my internship. The project is designed to manage employees, assign tasks, update task statuses, and generate basic task reports.

The main goal of the project is to build a clean REST API using Spring Boot, PostgreSQL, JPA, DTOs, validation, exception handling, reporting, and Docker support.

## Technologies Used

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* PostgreSQL
* Lombok
* Maven
* Docker
* Docker Compose
* Postman

## Main Features

* Employee management
* Manager and employee role structure
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

## API Endpoints

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

Example task assignment request:

Header:

```text
manager-id: 1
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
  "totalEmployees": 2,
  "activeEmployees": 2,
  "inactiveEmployees": 0,
  "totalTasks": 1,
  "newTasks": 1,
  "inProgressTasks": 0,
  "completedTasks": 0,
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
  "timestamp": "2026-06-26T08:38:00.7953903",
  "status": 400,
  "error": "Bad Request",
  "message": "Required request parameter 'startDate' for method parameter type LocalDateTime is not present",
  "path": "/api/reports/completed-tasks/export",
  "validationErrors": null
}
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

* Add security configuration
* Add unit tests
* Add Postman collection
* Improve reporting options
* Add a simple frontend for demo purposes
