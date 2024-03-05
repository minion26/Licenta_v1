package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamCreationDTO {
    private String name;
    private String questions;
    private Integer timeInMinutes;
    private Integer totalScore;
    private Integer passingScore;
    private LocalDateTime date;
}
