package com.example.licentav1.mapper;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.StudentAnswersExam;
import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.dto.ReviewStudentAnswersDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ReviewStudentAnswersMapper {
    public static ReviewStudentAnswersDTO toDTO(StudentExam studentExam, StudentAnswersExam studentAnswersExam, Exam exam, List<Teachers> teachers) {
        ReviewStudentAnswersDTO reviewStudentAnswersDTO = new ReviewStudentAnswersDTO();
        reviewStudentAnswersDTO.setIdStudentAnswerExam(studentAnswersExam.getIdStudentAnswerExam());
        reviewStudentAnswersDTO.setIdStudent(studentExam.getStudent().getIdUsers());

        List<UUID> idOfTeachers = new ArrayList<>();
        for (Teachers teacher : teachers) {
            //System.out.println(teacher.getIdUsers());
            UUID idTeacher = teacher.getIdUsers();
            idOfTeachers.add(idTeacher);
        }
        reviewStudentAnswersDTO.setIdTeacher(idOfTeachers);


        reviewStudentAnswersDTO.setIdExam(exam.getIdExam());

        reviewStudentAnswersDTO.setIdQuestion(studentAnswersExam.getQuestionsExam().getQuestion().getIdQuestion());

        reviewStudentAnswersDTO.setStudentAnswer(studentAnswersExam.getStudentAnswer());

        reviewStudentAnswersDTO.setQuestion(studentAnswersExam.getQuestionsExam().getQuestion().getQuestionText());

        return reviewStudentAnswersDTO;
    }
}
