package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.entity.Task;
import com.yurticicargo.personnel_task_management.entity.TaskStatus;
import com.yurticicargo.personnel_task_management.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/assign")
    public ResponseEntity<Task> assignTask(
            @RequestHeader("manager-id") Long managerId,
            @RequestParam Long employeeId,
            @Valid @RequestBody Task task) {
        return ResponseEntity.ok(taskService.assignTask(managerId, employeeId, task));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable Long id,
            @RequestHeader("employee-id") Long employeeId,
            @RequestParam TaskStatus newStatus) {
        return ResponseEntity.ok(taskService.updateStatus(id, employeeId, newStatus));
    }

    @GetMapping
    public ResponseEntity<List<Task>> findAll() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @GetMapping("/report")
    public ResponseEntity<List<Task>> completedTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(taskService.findCompletedTasks(startDate, endDate));
    }
}