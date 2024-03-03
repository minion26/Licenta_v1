package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class StorageException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public StorageException(String message) {
        super(message);
    }
}
