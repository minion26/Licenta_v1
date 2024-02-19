package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class StudentAlreadyExistsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public StudentAlreadyExistsException(String msg) {
        super(msg);
    }
}
