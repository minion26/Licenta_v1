package com.example.licentav1.mapper;

import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.domain.StudentAnswersExam;
import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.dto.QuestionAnswersDTO;
import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentAnswersExamMapper {
    public static StudentAnswersExam fromDTO(QuestionAnswersDTO questionAnswersDTO, StudentExam studentExam, QuestionsExam questionsExam) {
        return StudentAnswersExam.builder()
                .studentExam(studentExam)
                .questionsExam(questionsExam)
                .studentAnswer(questionAnswersDTO.getAnswer())
                .build();

    }

    public static List<StudentAnswersExamCreationDTO> toDTOs(List<StudentAnswersExam> studentAnswersExams) {
        return studentAnswersExams.stream()
                .map(StudentAnswersExamMapper::toDTO)
                .toList();
    }

    private static StudentAnswersExamCreationDTO toDTO(StudentAnswersExam studentAnswersExam) {
        return StudentAnswersExamCreationDTO.builder()
                .idStudentExam(studentAnswersExam.getStudentExam().getIdStudentExam())
                .answers(List.of(QuestionAnswersDTO.builder()
                        .idQuestionExam(studentAnswersExam.getQuestionsExam().getIdQuestionsExam())
                        .answer(studentAnswersExam.getStudentAnswer())
                        .build()))
                .build();
    }
}
