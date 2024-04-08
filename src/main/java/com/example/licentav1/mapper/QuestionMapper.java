package com.example.licentav1.mapper;

import com.example.licentav1.domain.CorrectAnswersExam;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.dto.QuestionDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {
    public static QuestionDTO toDTO(Question question, List<CorrectAnswersExam> correctAnswer) {
        return QuestionDTO.builder()
                .idQuestion(question.getIdQuestion())
                .questionText(question.getQuestionText())
                .idExam(question.getExam().getIdExam())
                .correctAnswers(correctAnswer.stream()
                        .map(CorrectAnswersExamMapper::toDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Question fromDTO(QuestionDTO questionDTO, Exam exam) {
        return Question.builder()
                .questionText(questionDTO.getQuestionText())
                .exam(exam)
                .build();
    }
}
