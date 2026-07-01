package com.yurticicargo.personnel_task_management.config;

import com.yurticicargo.personnel_task_management.entity.Employee;
import com.yurticicargo.personnel_task_management.entity.Role;
import com.yurticicargo.personnel_task_management.repository.EmployeeRepository;
import com.yurticicargo.personnel_task_management.util.Md5Util;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final Md5Util md5Util;

    public DataInitializer(EmployeeRepository employeeRepository, Md5Util md5Util) {
        this.employeeRepository = employeeRepository;
        this.md5Util = md5Util;
    }

    @Override
    public void run(String... args) {
        if (!employeeRepository.existsByUsername("manager")) {
            Employee manager = new Employee();
            manager.setFullName("System Manager");
            manager.setEmail("manager@yurticikargo.com");
            manager.setUsername("manager");
            manager.setPasswordHash(md5Util.hash("manager123"));
            manager.setRole(Role.MANAGER);
            manager.setActive(true);

            employeeRepository.save(manager);
        }
    }
}