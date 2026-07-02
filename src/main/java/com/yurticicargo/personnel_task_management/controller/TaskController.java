package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.dto.TaskRequest;
import com.yurticicargo.personnel_task_management.dto.TaskResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import com.yurticicargo.personnel_task_management.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @AuthenticationPrincipal Employee manager,
            @RequestParam Long employeeId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.assignTask(manager.getId(), employeeId, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @AuthenticationPrincipal Employee employee,
            @PathVariable Long id,
            @RequestParam TaskStatus newStatus) {
        return ResponseEntity.ok(taskService.updateStatus(id, employee.getId(), newStatus));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> findAll() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> findMyTasks(@AuthenticationPrincipal Employee employee) {
        return ResponseEntity.ok(taskService.findByAssignedEmployeeId(employee.getId()));
    }

    @GetMapping("/report")
    public ResponseEntity<List<TaskResponse>> completedTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskService.findCompletedTasks(startDate, endDate));
    }
}