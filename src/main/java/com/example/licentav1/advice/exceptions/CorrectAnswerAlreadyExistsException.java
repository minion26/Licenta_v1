package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class CorrectAnswerAlreadyExistsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public CorrectAnswerAlreadyExistsException(String message) {
        super(message);
    }
}
