package com.example.licentav1.mapper;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.TeacherExam;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.dto.ExamCreationDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TeacherExamMapper {
    public static TeacherExam fromDTO(Exam exam, Teachers teachers){
        return TeacherExam.builder()
                .exam(exam)
                .teacher(teachers)
                .build();

    }
}
