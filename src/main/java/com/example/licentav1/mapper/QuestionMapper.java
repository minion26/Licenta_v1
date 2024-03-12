package com.example.licentav1.mapper;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.dto.QuestionDTO;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {
    public static QuestionDTO toDTO(Question question) {
        return QuestionDTO.builder()
                .idQuestion(question.getIdQuestion())
                .questionText(question.getQuestionText())
                .idExam(question.getExam().getIdExam())
                .build();
    }

    public static Question fromDTO(QuestionDTO questionDTO, Exam exam) {
        return Question.builder()
                .questionText(questionDTO.getQuestionText())
                .exam(exam)
                .build();
    }
}
