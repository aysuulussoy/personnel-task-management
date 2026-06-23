package com.yurticicargo.personnel_task_management.service;

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
    public Task assignTask(Long managerId, Long employeeId, Task task) {
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

        task.setAssignedByManager(manager);
        task.setAssignedEmployee(employee);
        task.setStatus(TaskStatus.NEW);

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateStatus(Long taskId, Long employeeId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

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

        return taskRepository.save(task);
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findCompletedTasks(LocalDateTime startDate, LocalDateTime endDate) {
        return taskRepository.findByStatusAndCompletedAtBetween(TaskStatus.COMPLETED, startDate, endDate);
    }
}