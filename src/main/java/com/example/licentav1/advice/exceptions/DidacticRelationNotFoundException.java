package com.example.licentav1.advice.exceptions;

import org.springframework.stereotype.Service;

import java.io.Serial;

public class DidacticRelationNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public DidacticRelationNotFoundException(String message) {
        super(message);
    }
}
