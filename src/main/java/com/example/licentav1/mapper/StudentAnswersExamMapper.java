package com.example.licentav1.mapper;

import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.domain.StudentAnswersExam;
import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.dto.QuestionAnswersDTO;
import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import org.springframework.stereotype.Component;

@Component
public class StudentAnswersExamMapper {
    public static StudentAnswersExam fromDTO(QuestionAnswersDTO questionAnswersDTO, StudentExam studentExam, QuestionsExam questionsExam) {
        return StudentAnswersExam.builder()
                .studentExam(studentExam)
                .questionsExam(questionsExam)
                .studentAnswer(questionAnswersDTO.getAnswer())
                .build();

    }
}
