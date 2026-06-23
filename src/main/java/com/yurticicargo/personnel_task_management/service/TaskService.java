package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.entity.Role;
import com.yurticicargo.personnel_task_management.entity.Task;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import com.yurticicargo.personnel_task_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;

    public Task assignTask(Long managerId, Long employeeId, Task task) {
        Employee manager = employeeService.findById(managerId);
        Employee employee = employeeService.findById(employeeId);

        if (manager.getRole() != Role.MANAGER) {
            throw new RuntimeException("Only managers can assign tasks.");
        }

        task.setAssignedByManager(manager);
        task.setAssignedEmployee(employee);
        task.setStatus(TaskStatus.NEW);
        task.setCreatedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public Task updateStatus(Long taskId, Long employeeId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));

        if (!task.getAssignedEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("Only the assigned employee can update this task.");
        }

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findCompletedTasks(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByStatusAndCreatedAtBetween(TaskStatus.COMPLETED, startDate, endDate);
    }
}