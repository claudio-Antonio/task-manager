package com.task_manager.demo.dtos;


import com.task_manager.demo.enums.UserRole;

public record RegisterDTO(String login, String email, String password, UserRole role) {
}
