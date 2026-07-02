package com.yurticicargo.personnel_task_management.repository;

import com.yurticicargo.personnel_task_management.entity.Task;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusAndCompletedAtBetween(TaskStatus status, LocalDateTime startDate, LocalDateTime endDate);

    List<Task> findByAssignedEmployee_Id(Long employeeId);

    long countByStatus(TaskStatus status);
}