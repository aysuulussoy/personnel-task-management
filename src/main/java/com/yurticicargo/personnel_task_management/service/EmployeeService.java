package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.dto.EmployeeRequest;
import com.yurticicargo.personnel_task_management.dto.EmployeeResponse;
import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.exception.ForbiddenOperationException;
import com.yurticicargo.personnel_task_management.exception.ResourceNotFoundException;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse create(EmployeeRequest request) {
        Employee employee = mapToEntity(request);
        Employee savedEmployee = employeeRepository.save(employee);

        return mapToResponse(savedEmployee);
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<EmployeeResponse> findActiveEmployees() {
        return employeeRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public EmployeeResponse findResponseById(Long id) {
        Employee employee = findById(id);

        return mapToResponse(employee);
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee existingEmployee = findById(id);

        if (Boolean.FALSE.equals(existingEmployee.getActive())) {
            throw new ForbiddenOperationException("Inactive employee cannot be updated: " + id);
        }

        existingEmployee.setFullName(request.getFullName());
        existingEmployee.setEmail(request.getEmail());
        existingEmployee.setRole(request.getRole());

        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return mapToResponse(updatedEmployee);
    }

    @Transactional
    public void delete(Long id) {
        Employee existingEmployee = findById(id);
        existingEmployee.setActive(false);

        employeeRepository.save(existingEmployee);
    }

    private Employee mapToEntity(EmployeeRequest request) {
        Employee employee = new Employee();

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        employee.setActive(true);

        return employee;
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getRole(),
                employee.getActive(),
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }
}