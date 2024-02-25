package com.example.licentav1.advice.exceptions;

import org.springframework.stereotype.Service;

import java.io.Serial;

public class StudentCourseRelationNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public StudentCourseRelationNotFoundException(String message) {
        super(message);
    }
}
