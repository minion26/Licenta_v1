package com.example.licentav1.service;

import com.example.licentav1.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    List<QuestionDTO> getAllQuestionsByExam(UUID idExam);

    void createQuestion(QuestionDTO questionDTO, UUID idExam);

    void deleteQuestion(UUID idQuestion, UUID idExam);
}
