package com.example.licentav1.advice.exceptions;

import java.io.Serial;

public class QuestionsExamNotFoundException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;
    public QuestionsExamNotFoundException(String message) {
        super(message);
    }
}
