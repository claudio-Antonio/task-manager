package com.task_manager.demo.controllers;

import com.task_manager.demo.dtos.TaskRequestDTO;
import com.task_manager.demo.dtos.TaskResponseDTO;
import com.task_manager.demo.infra.security.SecurityConfigurations;
import com.task_manager.demo.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "tasks", description = "controller to create, delete, update and find tasks")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "List of tasks find successfully")
    @ApiResponse(responseCode = "404", description = "Not found, List of tasks doesn't exists")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<List<TaskResponseDTO>> findAllTasks() {
        List<TaskResponseDTO> response = taskService.findAllTasks().stream().map(p -> new TaskResponseDTO(p)).toList();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an specific task by id")
    @ApiResponse(responseCode = "200", description = "Task find successfully")
    @ApiResponse(responseCode = "404", description = "Not found, Task don't exist")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<TaskResponseDTO> findTaskById(@PathVariable Long id) {
        TaskResponseDTO response = new TaskResponseDTO(taskService.findTaskById(id));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/new-task")
    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "200", description = "Create task successfully")
    @ApiResponse(responseCode = "400", description = "BadRequestException, Task not created")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<TaskResponseDTO> saveTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO response = taskService.createTask(taskRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update-task/{id}")
    @Operation(summary = "Update an specific task by id")
    @ApiResponse(responseCode = "200", description = "Task updated successfully")
    @ApiResponse(responseCode = "400", description = "BadRequestException, task not updated")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO response = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task by id")
    @ApiResponse(responseCode = "200", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Not found, tasks not deleted")
    @ApiResponse(responseCode = "500", description = "Server error")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

}
