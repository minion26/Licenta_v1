package com.example.licentav1.dto;

import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.Teachers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDTO {
    private String name;
    private List<Question> questions;
    private Integer timeInMinutes;
    private Integer totalScore;
    private Integer passingScore;
    private LocalDateTime date;
    private String courseName;
    private List<UUID> idTeachers;

}
