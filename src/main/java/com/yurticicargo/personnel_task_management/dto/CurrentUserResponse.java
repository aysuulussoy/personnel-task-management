package com.yurticicargo.personnel_task_management.dto;

import com.yurticicargo.personnel_task_management.entity.Role;

public record CurrentUserResponse(
        Long id,
        String fullName,
        String email,
        String username,
        Role role,
        Boolean active
) {
}