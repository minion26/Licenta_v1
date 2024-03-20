package com.example.licentav1.mapper;

import com.example.licentav1.domain.CorrectAnswersExam;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import org.springframework.stereotype.Component;

@Component
public class CorrectAnswersExamMapper {
    public static CorrectAnswersExamDTO toDTO(CorrectAnswersExam correctAnswersExam) {
        return null;
    }

    public static CorrectAnswersExam fromDTO(CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO, QuestionsExam questionsExam) {
        return CorrectAnswersExam.builder()
                .correctAnswer(correctAnswersExamCreationDTO.getCorrectAnswer())
                .score(correctAnswersExamCreationDTO.getScore())
                .questionsExam(questionsExam)
                .build();
    }
}
