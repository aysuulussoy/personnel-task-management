package com.yurticicargo.personnel_task_management.report;

import com.yurticicargo.personnel_task_management.dto.ReportSummaryResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.entity.Task;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import com.yurticicargo.personnel_task_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public ReportSummaryResponse getSummary() {
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByActiveTrue();
        long inactiveEmployees = employeeRepository.countByActiveFalse();

        long totalTasks = taskRepository.count();
        long newTasks = taskRepository.countByStatus(TaskStatus.NEW);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByStatus(TaskStatus.COMPLETED);
        long canceledTasks = taskRepository.countByStatus(TaskStatus.CANCELED);

        return new ReportSummaryResponse(
                totalEmployees,
                activeEmployees,
                inactiveEmployees,
                totalTasks,
                newTasks,
                inProgressTasks,
                completedTasks,
                canceledTasks
        );
    }

    public String generateCompletedTasksCsv(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate.");
        }

        List<Task> completedTasks = taskRepository.findByStatusAndCompletedAtBetween(
                TaskStatus.COMPLETED,
                startDate,
                endDate
        );

        StringBuilder csv = new StringBuilder();

        csv.append("Task ID,Title,Description,Status,Employee ID,Employee Name,Manager ID,Manager Name,Created At,Completed At\n");

        for (Task task : completedTasks) {
            Employee assignedEmployee = task.getAssignedEmployee();
            Employee assignedByManager = task.getAssignedByManager();

            csv.append(task.getId()).append(",");
            csv.append(escapeCsv(task.getTitle())).append(",");
            csv.append(escapeCsv(task.getDescription())).append(",");
            csv.append(task.getStatus()).append(",");

            csv.append(assignedEmployee != null ? assignedEmployee.getId() : "").append(",");
            csv.append(assignedEmployee != null ? escapeCsv(assignedEmployee.getFullName()) : "").append(",");

            csv.append(assignedByManager != null ? assignedByManager.getId() : "").append(",");
            csv.append(assignedByManager != null ? escapeCsv(assignedByManager.getFullName()) : "").append(",");

            csv.append(formatDate(task.getCreatedAt())).append(",");
            csv.append(formatDate(task.getCompletedAt())).append("\n");
        }

        return csv.toString();
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        return dateTime.format(DATE_FORMATTER);
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escapedValue = value.replace("\"", "\"\"");

        if (escapedValue.contains(",") || escapedValue.contains("\"") || escapedValue.contains("\n")) {
            return "\"" + escapedValue + "\"";
        }

        return escapedValue;
    }
}