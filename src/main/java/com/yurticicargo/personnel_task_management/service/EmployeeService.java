package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee save(Employee employee) {
        employee.setActive(true);
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public List<Employee> findActiveEmployees() {
        return employeeRepository.findByActiveTrue();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    @Transactional
    public Employee update(Long id, Employee newEmployee) {
        Employee existingEmployee = findById(id);

        if (Boolean.FALSE.equals(existingEmployee.getActive())) {
            throw new RuntimeException("Inactive employee cannot be updated: " + id);
        }

        existingEmployee.setFullName(newEmployee.getFullName());
        existingEmployee.setEmail(newEmployee.getEmail());
        existingEmployee.setRole(newEmployee.getRole());

        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public void delete(Long id) {
        Employee existingEmployee = findById(id);
        existingEmployee.setActive(false);
        employeeRepository.save(existingEmployee);
    }
}