package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExamDetailsDTO {
    private UUID idStudentExam;
    private UUID idStudent;
    private String courseName;
    private UUID idExam;
    private Integer score; // -1 = not graded
    private Integer examStatus; // -1 = not started, 0 = failed, 1 = passed
}
