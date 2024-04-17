package com.example.licentav1.mapper;

import com.example.licentav1.domain.Feedback;
import com.example.licentav1.domain.Homework;
import com.example.licentav1.dto.FeedbackCreationDTO;
import com.example.licentav1.dto.FeedbackDTO;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {
    public static Feedback fromDTO(FeedbackCreationDTO feedbackCreationDTO, Homework homework) {
        return Feedback.builder()
                .content(feedbackCreationDTO.getContent())
                .homework(homework)
                .build();
    }

    public static FeedbackDTO toDTO(Feedback feedback) {
        return FeedbackDTO.builder()
                .idFeedback(feedback.getIdFeedback())
                .content(feedback.getContent())
                .build();
    }
}
