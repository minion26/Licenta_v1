package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class AnswerNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public AnswerNotFoundException(String message) {
        super(message);
    }
}
