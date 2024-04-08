package com.example.licentav1.dto;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
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
public class QuestionsExamDTO {
    private UUID idQuestionsExam;
    private UUID questionId;
    private UUID examId;

}
