package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.dto.EmployeeRequest;
import com.yurticicargo.personnel_task_management.dto.EmployeeResponse;
import com.yurticicargo.personnel_task_management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<EmployeeResponse>> findActiveEmployees() {
        return ResponseEntity.ok(employeeService.findActiveEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findResponseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}