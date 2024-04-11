package com.example.licentav1.dto;

import com.example.licentav1.domain.Lectures;
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
public class HomeworkAnnouncementsDTO {
    private UUID idHomeworkAnnouncement;
    private String title;
    private String description;
    private Integer score;
    private LocalDateTime dueDate;
    private UUID idLectures;
    private UUID idTeacher;
}
