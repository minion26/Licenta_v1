package com.example.licentav1.mapper;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.ExamCreationDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionsExamMapper {
    public static QuestionsExam fromDTO(Exam exam, Question question){
        return QuestionsExam.builder()
                .exam(exam)
                .question(question)
                .build();

    }
}
