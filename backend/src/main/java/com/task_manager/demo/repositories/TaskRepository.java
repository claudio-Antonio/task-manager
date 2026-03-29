package com.task_manager.demo.repositories;

import com.task_manager.demo.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
