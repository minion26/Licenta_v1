package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class CourseNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public CourseNotFoundException(String message) {
        super(message);
    }
}
