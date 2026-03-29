package com.task_manager.demo.enums;

public enum UserRole {
    COLLABORATOR("collaborator"),
    MANAGER("manager"),
    ADMIN("admin");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
