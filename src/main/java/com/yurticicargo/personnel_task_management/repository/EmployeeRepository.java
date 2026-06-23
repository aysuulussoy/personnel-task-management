package com.yurticicargo.personnel_task_management.repository;

import com.yurticicargo.personnel_task_management.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}