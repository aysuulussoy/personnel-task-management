package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.dto.TaskRequest;
import com.yurticicargo.personnel_task_management.dto.TaskResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.entity.Role;
import com.yurticicargo.personnel_task_management.entity.Task;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import com.yurticicargo.personnel_task_management.exception.ForbiddenOperationException;
import com.yurticicargo.personnel_task_management.exception.ResourceNotFoundException;
import com.yurticicargo.personnel_task_management.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeService employeeService;

    @Transactional
    public TaskResponse assignTask(Long managerId, Long employeeId, TaskRequest request) {
        Employee manager = employeeService.findById(managerId);
        Employee employee = employeeService.findById(employeeId);

        if (manager.getRole() != Role.MANAGER) {
            throw new ForbiddenOperationException("Only managers can assign tasks.");
        }

        if (Boolean.FALSE.equals(manager.getActive())) {
            throw new ForbiddenOperationException("Inactive manager cannot assign tasks: " + managerId);
        }

        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new ForbiddenOperationException("Task cannot be assigned to an inactive employee: " + employeeId);
        }

        Task task = mapToEntity(request);
        task.setAssignedByManager(manager);
        task.setAssignedEmployee(employee);
        task.setStatus(TaskStatus.NEW);

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, Long employeeId, TaskStatus newStatus) {
        Task task = findById(taskId);

        if (!task.getAssignedEmployee().getId().equals(employeeId)) {
            throw new ForbiddenOperationException("Only the assigned employee can update this task.");
        }

        if (Boolean.FALSE.equals(task.getAssignedEmployee().getActive())) {
            throw new ForbiddenOperationException("Inactive employee cannot update task status: " + employeeId);
        }

        if (newStatus == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }

        if (newStatus != TaskStatus.COMPLETED) {
            task.setCompletedAt(null);
        }

        task.setStatus(newStatus);

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    public List<TaskResponse> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public List<TaskResponse> findByAssignedEmployeeId(Long employeeId) {
        return taskRepository.findByAssignedEmployee_Id(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public List<TaskResponse> findCompletedTasks(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByStatusAndCompletedAtBetween(TaskStatus.COMPLETED, startDate, endDate)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    private Task mapToEntity(TaskRequest request) {
        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        return task;
    }

    private TaskResponse mapToResponse(Task task) {
        Employee assignedEmployee = task.getAssignedEmployee();
        Employee assignedByManager = task.getAssignedByManager();

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                assignedEmployee != null ? assignedEmployee.getId() : null,
                assignedEmployee != null ? assignedEmployee.getFullName() : null,
                assignedByManager != null ? assignedByManager.getId() : null,
                assignedByManager != null ? assignedByManager.getFullName() : null,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getCompletedAt()
        );
    }
}