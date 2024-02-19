package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class TeacherNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;
    public TeacherNotFoundException(String message) {
        super(message);
    }
}
