package com.task_manager.demo.infra.exceptions.handler;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected LocalDateTime timestamp;
    protected int status;
    protected String message;
    protected String details;
}
