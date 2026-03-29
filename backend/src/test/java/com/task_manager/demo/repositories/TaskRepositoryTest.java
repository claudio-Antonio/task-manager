package com.task_manager.demo.repositories;

import static org.junit.jupiter.api.Assertions.*;
import com.task_manager.demo.domain.Task;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.enums.TaskStatus;
import com.task_manager.demo.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        user = User.builder()
                .login("manager")
                .password("encoded_password")
                .email("manager@email.com")
                .role(UserRole.MANAGER)
                .build();

        userRepository.save(user);
    }

    @Test
    void save_shouldPersistTask() {
        Task task = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Task");
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.NOT_COMPLETE);
    }

    @Test
    void findById_shouldReturnTaskWhenExists() {
        Task task = Task.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();
        taskRepository.save(task);

        Optional<Task> result = taskRepository.findById(task.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Task");
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Task> result = taskRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        taskRepository.save(Task.builder().title("Task 1").description("Desc 1").status(TaskStatus.NOT_COMPLETE).user(user).build());
        taskRepository.save(Task.builder().title("Task 2").description("Desc 2").status(TaskStatus.COMPLETE).user(user).build());

        List<Task> tasks = taskRepository.findAll();

        assertThat(tasks).hasSize(2);
    }

    @Test
    void deleteById_shouldRemoveTask() {
        Task task = Task.builder()
                .title("Task to delete")
                .description("Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();
        taskRepository.save(task);

        taskRepository.deleteById(task.getId());

        Optional<Task> result = taskRepository.findById(task.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void save_shouldPersistUpdatedTask() {
        Task task = Task.builder()
                .title("Original Title")
                .description("Original Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();
        taskRepository.save(task);

        task.setTitle("Updated Title");
        task.setStatus(TaskStatus.COMPLETE);
        taskRepository.save(task);

        Optional<Task> result = taskRepository.findById(task.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getStatus()).isEqualTo(TaskStatus.COMPLETE);
    }

    @Test
    void save_shouldAssociateTaskWithUser() {
        Task task = Task.builder()
                .title("Task with User")
                .description("Description")
                .status(TaskStatus.NOT_COMPLETE)
                .user(user)
                .build();

        Task saved = taskRepository.save(task);

        assertThat(saved.getUser()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
    }
}