package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStudentAnswersDTO {
    private UUID idStudent;
    private List<UUID> idTeacher;
    private UUID idExam;
    private UUID idQuestion;
    private String question;
    private String studentAnswer;
    private boolean needsReview;
}
