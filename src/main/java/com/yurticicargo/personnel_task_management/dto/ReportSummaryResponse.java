package com.yurticicargo.personnel_task_management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;

    private long totalTasks;
    private long newTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long canceledTasks;
}