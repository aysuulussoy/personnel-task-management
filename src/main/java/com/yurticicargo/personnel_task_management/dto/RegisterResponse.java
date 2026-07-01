package com.yurticicargo.personnel_task_management.dto;

import com.yurticicargo.personnel_task_management.entity.Role;

public class RegisterResponse {

    private Long id;
    private String fullName;
    private String email;
    private String username;
    private Role role;
    private Boolean active;

    public RegisterResponse(Long id, String fullName, String email, String username, Role role, Boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public Boolean getActive() {
        return active;
    }
}