package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAndStudentsAnswersDTO {
    private String questionText;
    private String correctAnswer;
    private String studentAnswer;
    private int score;
}
