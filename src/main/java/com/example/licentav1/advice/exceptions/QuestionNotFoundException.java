package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class QuestionNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public QuestionNotFoundException(String message) {
        super(message);
    }
}
