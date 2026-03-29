package com.task_manager.demo.infra.exceptions.handler;

import com.task_manager.demo.infra.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<String> notFoundException(NotFoundException exception) {
        ExceptionDetails e = ExceptionDetails.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .details(exception.toString())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.toString());
    }


}
