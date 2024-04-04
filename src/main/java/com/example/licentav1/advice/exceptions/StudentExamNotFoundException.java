package com.example.licentav1.advice.exceptions;

import org.springframework.stereotype.Service;

import java.io.Serial;

public class StudentExamNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public StudentExamNotFoundException(String message) {
        super(message);
    }
}
