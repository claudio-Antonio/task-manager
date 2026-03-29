package com.task_manager.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.Task;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.dtos.TaskRequestDTO;
import com.task_manager.demo.dtos.TaskResponseDTO;
import com.task_manager.demo.enums.TaskStatus;
import com.task_manager.demo.enums.UserRole;
import com.task_manager.demo.infra.exceptions.NotFoundException;
import com.task_manager.demo.repositories.TaskRepository;
import com.task_manager.demo.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User user;
    private Task task;
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

        taskRequestDTO = new TaskRequestDTO("Task Title", "Task Description", TaskStatus.NOT_COMPLETE, 1L);
    }

    // -------------------------
    // findAllTasks
    // -------------------------

    @Test
    void findAllTasks_shouldReturnListOfTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.findAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Task Title");
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void findAllTasks_shouldReturnEmptyListWhenNoTasks() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<Task> result = taskService.findAllTasks();

        assertThat(result).isEmpty();
        verify(taskRepository, times(1)).findAll();
    }

    // -------------------------
    // findTaskById
    // -------------------------

    @Test
    void findTaskById_shouldReturnTaskWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findTaskById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Task Title");
    }

    @Test
    void findTaskById_shouldThrowNotFoundExceptionWhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findTaskById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
    }

    // -------------------------
    // createTask
    // -------------------------

    @Test
    void createTask_shouldCreateAndReturnTask() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.createTask(taskRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Task Title");
        assertThat(result.userId()).isEqualTo(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_shouldAssociateAuthenticatedUserToTask() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO result = taskService.createTask(taskRequestDTO);

        assertThat(result.userId()).isEqualTo(user.getId());
    }

    // -------------------------
    // updateTask
    // -------------------------

    @Test
    void updateTask_shouldUpdateAndReturnTask() {
        TaskRequestDTO updateRequest = new TaskRequestDTO("Updated Title", "Updated Description", TaskStatus.COMPLETE, 1L);
        Task updatedTask = Task.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.COMPLETE)
                .user(user)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponseDTO result = taskService.updateTask(1L, updateRequest);

        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.description()).isEqualTo("Updated Description");
        assertThat(result.status()).isEqualTo(TaskStatus.COMPLETE);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowNotFoundExceptionWhenTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(99L, taskRequestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(1L, taskRequestDTO))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");

        verify(taskRepository, never()).save(any());
    }

    // -------------------------
    // deleteTaskById
    // -------------------------

    @Test
    void deleteTaskById_shouldCallRepositoryDelete() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTaskById(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }
}