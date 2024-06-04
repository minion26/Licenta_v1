package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class NonAllowedException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public NonAllowedException(String message) {
        super(message);
    }
}
