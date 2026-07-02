package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.dto.AuthResponse;
import com.yurticicargo.personnel_task_management.dto.CurrentUserResponse;
import com.yurticicargo.personnel_task_management.dto.LoginRequest;
import com.yurticicargo.personnel_task_management.dto.RegisterRequest;
import com.yurticicargo.personnel_task_management.dto.RegisterResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(@AuthenticationPrincipal Employee employee) {
        return ResponseEntity.ok(new CurrentUserResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getUsername(),
                employee.getRole(),
                employee.getActive()
        ));
    }
}