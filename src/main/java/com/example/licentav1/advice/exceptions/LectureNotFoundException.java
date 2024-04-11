package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class LectureNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    public LectureNotFoundException(String message) {
        super(message);
    }
}
