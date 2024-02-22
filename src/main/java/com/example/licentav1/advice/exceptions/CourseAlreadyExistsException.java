package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class CourseAlreadyExistsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public CourseAlreadyExistsException(String message) {
        super(message);
    }
}
