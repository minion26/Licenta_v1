package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class ExamNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public ExamNotFoundException(String message) {
        super(message);
    }
}
