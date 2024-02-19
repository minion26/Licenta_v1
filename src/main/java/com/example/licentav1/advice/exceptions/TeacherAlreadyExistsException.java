package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class TeacherAlreadyExistsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public TeacherAlreadyExistsException(String msg) {
        super(msg);
    }
}
