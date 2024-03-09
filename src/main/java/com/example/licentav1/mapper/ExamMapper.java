package com.example.licentav1.mapper;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.TeacherExam;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.dto.ExamDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExamMapper {
    public static Exam fromDTO(ExamCreationDTO examCreationDTO, Courses course){
        return Exam.builder()
                .name(examCreationDTO.getName())
                .timeInMinutes(examCreationDTO.getTimeInMinutes())
                .totalScore(examCreationDTO.getTotalScore())
                .passingScore(examCreationDTO.getPassingScore())
                .date(examCreationDTO.getDate())
                .course(course)
                .build();
    }

    public static ExamDTO toDTO(Exam exam){
        return ExamDTO.builder()
                .name(exam.getName())
                .timeInMinutes(exam.getTimeInMinutes())
                .totalScore(exam.getTotalScore())
                .passingScore(exam.getPassingScore())
                .date(exam.getDate())
                .courseName(exam.getCourse().getName())
                .build();
    }
}
