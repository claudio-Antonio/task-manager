package com.task_manager.demo.dtos;

import com.task_manager.demo.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskRequestDTO(@NotBlank String title, @NotBlank String description, @NotNull TaskStatus status, @NotNull Long userID) {
}
