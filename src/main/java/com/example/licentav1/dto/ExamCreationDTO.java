package com.example.licentav1.dto;

import jakarta.validation.constraints.NotNull;
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
public class ExamCreationDTO {
    private String name;
    private List<QuestionCreationDTO> question;
    private Integer timeInMinutes;
    private Integer totalScore;
    private Integer passingScore;
    private LocalDateTime date;
    private UUID idCourse;
    private List<UUID> idTeacher;
}
