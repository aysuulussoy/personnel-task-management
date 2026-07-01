package com.yurticicargo.personnel_task_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class PersonnelTaskManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonnelTaskManagementApplication.class, args);
	}
}