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
//                .idNote(feedbackCreationDTO.getIdNote())
                .positionX(feedbackCreationDTO.getPositionX())
                .positionY(feedbackCreationDTO.getPositionY())
                .noteText(feedbackCreationDTO.getNoteText())
                .content(feedbackCreationDTO.getNoteText())
                .homework(homework)
                .build();
    }

    public static FeedbackDTO toDTO(Feedback feedback) {
        return FeedbackDTO.builder()
                .idFeedback(feedback.getIdFeedback())
                .content(feedback.getContent())
//                .idNote(feedback.getIdNote())
                .positionX(feedback.getPositionX())
                .positionY(feedback.getPositionY())
                .noteText(feedback.getNoteText())
                .build();
    }
}
