package com.task_manager.demo.services;

import com.task_manager.demo.domain.Task;
import com.task_manager.demo.domain.User;
import com.task_manager.demo.dtos.TaskRequestDTO;
import com.task_manager.demo.dtos.TaskResponseDTO;
import com.task_manager.demo.infra.exceptions.NotFoundException;
import com.task_manager.demo.repositories.TaskRepository;
import com.task_manager.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public Task findTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.orElseThrow(() -> new NotFoundException("Task not found"));
    }

    public TaskResponseDTO createTask(TaskRequestDTO request) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Task task = new Task(request, user);

        return new TaskResponseDTO(taskRepository.save(task));
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task task = findTaskById(id);

        User user = userRepository.findById(request.userID()).orElseThrow(() -> new NotFoundException("User not found"));

        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setUser(user);

        return new TaskResponseDTO(taskRepository.save(task));
    }

    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }
}
