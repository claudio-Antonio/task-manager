package com.task_manager.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.Task;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.dtos.TaskRequestDTO;
import com.task_manager.demo.dtos.TaskResponseDTO;
import com.task_manager.demo.enums.TaskStatus;
import com.task_manager.demo.enums.UserRole;
import com.task_manager.demo.infra.exceptions.NotFoundException;
import com.task_manager.demo.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    private User user;
    private Task task;
    private TaskResponseDTO taskResponseDTO;
    private TaskRequestDTO taskRequestDTO;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .login("manager")
                .password("encoded_password")
                .email("manager@email.com")
                .role(UserRole.MANAGER)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Task Title")
                .description("Task Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();

        taskResponseDTO = new TaskResponseDTO(1L, "Task Title", "Task Description", TaskStatus.NOT_COMPLETE, 1L);
        taskRequestDTO = new TaskRequestDTO("Task Title", "Task Description", TaskStatus.NOT_COMPLETE, 1L);
    }

    // -------------------------
    // GET /tasks
    // -------------------------

    @Test
    void findAllTasks_shouldReturn200WithListOfTasks() {
        when(taskService.findAllTasks()).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponseDTO>> response = taskController.findAllTasks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).title()).isEqualTo("Task Title");
        assertThat(response.getBody().get(0).status()).isEqualTo(TaskStatus.NOT_COMPLETE);
        verify(taskService, times(1)).findAllTasks();
    }

    @Test
    void findAllTasks_shouldReturn200WithEmptyList() {
        when(taskService.findAllTasks()).thenReturn(List.of());

        ResponseEntity<List<TaskResponseDTO>> response = taskController.findAllTasks();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // -------------------------
    // GET /tasks/{id}
    // -------------------------

    @Test
    void findTaskById_shouldReturn200WhenFound() {
        when(taskService.findTaskById(1L)).thenReturn(task);

        ResponseEntity<TaskResponseDTO> response = taskController.findTaskById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Task Title");
    }

    @Test
    void findTaskById_shouldThrowNotFoundWhenTaskDoesNotExist() {
        when(taskService.findTaskById(99L)).thenThrow(new NotFoundException("Task not found"));

        assertThatThrownBy(() -> taskController.findTaskById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
    }

    // -------------------------
    // POST /tasks/new-task
    // -------------------------

    @Test
    void saveTask_shouldReturn200WhenCreated() {
        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(taskResponseDTO);

        ResponseEntity<TaskResponseDTO> response = taskController.saveTask(taskRequestDTO);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().title()).isEqualTo("Task Title");
        verify(taskService, times(1)).createTask(any(TaskRequestDTO.class));
    }

    @Test
    void saveTask_shouldDelegateToService() {
        when(taskService.createTask(taskRequestDTO)).thenReturn(taskResponseDTO);

        taskController.saveTask(taskRequestDTO);

        verify(taskService, times(1)).createTask(taskRequestDTO);
    }

    // -------------------------
    // PUT /tasks/update-task/{id}
    // -------------------------

    @Test
    void updateTask_shouldReturn200WhenUpdated() {
        TaskResponseDTO updated = new TaskResponseDTO(1L, "Updated Title", "Updated Description", TaskStatus.COMPLETE, 1L);
        TaskRequestDTO updateRequest = new TaskRequestDTO("Updated Title", "Updated Description", TaskStatus.COMPLETE, 1L);

        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class))).thenReturn(updated);

        ResponseEntity<TaskResponseDTO> response = taskController.updateTask(1L, updateRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("Updated Title");
        assertThat(response.getBody().status()).isEqualTo(TaskStatus.COMPLETE);
    }

    @Test
    void updateTask_shouldThrowNotFoundWhenTaskDoesNotExist() {
        when(taskService.updateTask(eq(99L), any(TaskRequestDTO.class)))
                .thenThrow(new NotFoundException("Task not found"));

        assertThatThrownBy(() -> taskController.updateTask(99L, taskRequestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
    }

    @Test
    void updateTask_shouldThrowNotFoundWhenUserDoesNotExist() {
        when(taskService.updateTask(eq(1L), any(TaskRequestDTO.class)))
                .thenThrow(new NotFoundException("User not found"));

        assertThatThrownBy(() -> taskController.updateTask(1L, taskRequestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    // -------------------------
    // DELETE /tasks/{id}
    // -------------------------

    @Test
    void deleteTaskById_shouldReturn204WhenDeleted() {
        doNothing().when(taskService).deleteTaskById(1L);

        ResponseEntity<Void> response = taskController.deleteTaskById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(taskService, times(1)).deleteTaskById(1L);
    }

    @Test
    void deleteTaskById_shouldDelegateToService() {
        doNothing().when(taskService).deleteTaskById(1L);

        taskController.deleteTaskById(1L);

        verify(taskService, times(1)).deleteTaskById(1L);
    }
}