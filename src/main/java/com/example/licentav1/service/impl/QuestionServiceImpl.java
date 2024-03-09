package com.example.licentav1.service.impl;

import com.example.licentav1.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl {
    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
}
