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
public class HomeworkAnnouncementsCreationDTO {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Integer score;

}