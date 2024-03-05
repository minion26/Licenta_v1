package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDTO {
    private String name;
    private String questions;
    private Integer timeInMinutes;
    private Integer totalScore;
    private Integer passingScore;
    private LocalDateTime date;
    private UUID idCourse;
    private UUID idTeacher;

}
