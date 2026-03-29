package com.task_manager.demo.dtos;

import com.task_manager.demo.domain.Task;
import com.task_manager.demo.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskResponseDTO(@NotNull Long id, @NotBlank String title, @NotBlank String description, @NotNull TaskStatus status, @NotNull Long userId) {
    public TaskResponseDTO(Task task) {
        this(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getUser().getId());
    }
}
