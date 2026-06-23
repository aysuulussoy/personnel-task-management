package com.yurticicargo.personnel_task_management.service;

import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    public Employee update(Long id, Employee newEmployee) {
        Employee existingEmployee = findById(id);

        existingEmployee.setFullName(newEmployee.getFullName());
        existingEmployee.setEmail(newEmployee.getEmail());
        existingEmployee.setRole(newEmployee.getRole());

        return employeeRepository.save(existingEmployee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}