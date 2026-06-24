package com.yurticicargo.personnel_task_management.dto;

import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;

    private Long assignedEmployeeId;
    private String assignedEmployeeName;

    private Long assignedByManagerId;
    private String assignedByManagerName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}