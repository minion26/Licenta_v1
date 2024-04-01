package com.example.licentav1.mapper;

import com.example.licentav1.domain.CorrectAnswersExam;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorrectAnswersExamMapper {


    public static CorrectAnswersExam fromDTO(CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO, QuestionsExam questionsExam) {
        return CorrectAnswersExam.builder()
                .correctAnswer(correctAnswersExamCreationDTO.getCorrectAnswer())
                .score(correctAnswersExamCreationDTO.getScore())
                .questionsExam(questionsExam)
                .build();
    }

    public static CorrectAnswersExamDTO toDTO(CorrectAnswersExam correctAnswersExam) {
        return CorrectAnswersExamDTO.builder()
                .idAnswerExam(correctAnswersExam.getIdQuestion())
                .correctAnswer(correctAnswersExam.getCorrectAnswer())
                .score(correctAnswersExam.getScore())
                .idQuestionExam(correctAnswersExam.getIdQuestion())
                .build();

    }

}
