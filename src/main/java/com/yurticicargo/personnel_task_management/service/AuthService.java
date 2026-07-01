package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.dto.AuthResponse;
import com.yurticicargo.personnel_task_management.dto.LoginRequest;
import com.yurticicargo.personnel_task_management.dto.RegisterRequest;
import com.yurticicargo.personnel_task_management.dto.RegisterResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import com.yurticicargo.personnel_task_management.security.JwtService;
import com.yurticicargo.personnel_task_management.util.Md5Util;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final Md5Util md5Util;
    private final JwtService jwtService;

    public AuthService(EmployeeRepository employeeRepository, Md5Util md5Util, JwtService jwtService) {
        this.employeeRepository = employeeRepository;
        this.md5Util = md5Util;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (Boolean.FALSE.equals(employee.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is inactive");
        }

        String passwordHash = md5Util.hash(request.getPassword());

        if (!passwordHash.equals(employee.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        String token = jwtService.generateToken(employee);

        return new AuthResponse(
                token,
                employee.getId(),
                employee.getUsername(),
                employee.getRole()
        );
    }

    public RegisterResponse register(RegisterRequest request) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already in use");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }

        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setUsername(request.getUsername());
        employee.setPasswordHash(md5Util.hash(request.getPassword()));
        employee.setRole(request.getRole());
        employee.setActive(true);

        Employee savedEmployee = employeeRepository.save(employee);

        return new RegisterResponse(
                savedEmployee.getId(),
                savedEmployee.getFullName(),
                savedEmployee.getEmail(),
                savedEmployee.getUsername(),
                savedEmployee.getRole(),
                savedEmployee.getActive()
        );
    }
}