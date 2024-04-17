package com.example.licentav1.service;

import com.example.licentav1.dto.FeedbackCreationDTO;
import com.example.licentav1.dto.FeedbackDTO;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    void createFeedback(UUID idHomework, List<FeedbackCreationDTO> listFeedbackCreationDTO);

    List<FeedbackDTO> getFeedbacks(UUID idHomework);

    void updateFeedback(UUID idFeedback, FeedbackCreationDTO feedbackCreationDTO);

    void deleteFeedback(UUID idFeedback);
}
