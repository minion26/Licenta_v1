package com.example.licentav1.mapper;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentExamMapper {

    public static StudentExam fromDTO(StudentExamCreationDTO studentExamCreationDTO, Students student, Exam exam) {
        return StudentExam.builder()
                .student(student)
                .exam(exam)
                .score(-1)
                .examStatus(-1) //e by default
                .build();

    }

    public static StudentExamDTO toDTO(StudentExam studentExam) {
        return StudentExamDTO.builder()
                .idStudentExam(studentExam.getIdStudentExam())
                .idStudent(studentExam.getStudent().getIdUsers())
                .idExam(studentExam.getExam().getIdExam())
                .score(studentExam.getScore())
                .examStatus(studentExam.getExamStatus())
                .build();
    }

}
