package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class LastAdminException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;
    public LastAdminException(String message) {
        super(message);
    }
}
