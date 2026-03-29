package com.task_manager.demo.domain;

import com.task_manager.demo.dtos.TaskRequestDTO;
import com.task_manager.demo.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "tb_task")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Getter
@Setter
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Task(TaskRequestDTO request, User user) {
        this.title = request.title();
        this.description = request.description();
        this.status = request.status();
        this.user = user;
    }

}
