package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Feedback;
import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.StudentHomework;
import com.example.licentav1.dto.FeedbackCreationDTO;
import com.example.licentav1.dto.FeedbackDTO;
import com.example.licentav1.mapper.FeedbackMapper;
import com.example.licentav1.repository.FeedbackRepository;
import com.example.licentav1.repository.HomeworkRepository;
import com.example.licentav1.repository.StudentHomeworkRepository;
import com.example.licentav1.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final HomeworkRepository homeworkRepository;
    private final StudentHomeworkRepository studentHomeworkRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, HomeworkRepository homeworkRepository, StudentHomeworkRepository studentHomeworkRepository) {
        this.feedbackRepository = feedbackRepository;
        this.homeworkRepository = homeworkRepository;
        this.studentHomeworkRepository = studentHomeworkRepository;
    }

    @Override
    public void createFeedback(UUID idHomework, List<FeedbackCreationDTO> listFeedbackCreationDTO) {
        StudentHomework studentHomework = studentHomeworkRepository.findByIdHomework(idHomework).orElseThrow(() -> new RuntimeException("Student homework not found"));
        Homework homework = homeworkRepository.findByIdHomework(idHomework).orElseThrow(() -> new RuntimeException("Homework not found"));

        List<Feedback> feedbacks = new ArrayList<>();

        for (FeedbackCreationDTO feedbackCreationDTO : listFeedbackCreationDTO) {
            Feedback feedback = FeedbackMapper.fromDTO(feedbackCreationDTO, homework);

            feedbacks.add(feedback);
        }

        feedbackRepository.saveAll(feedbacks);

    }

    @Override
    public List<FeedbackDTO> getFeedbacks(UUID idHomework) {
        // iau lista de feedback-uri pentru un anumit id de homework
        List<Feedback> feedbacks = feedbackRepository.findAllByIdHomeWork(idHomework);

        // creez o lista de feedback-uri de tip DTO
        List<FeedbackDTO> feedbackDTOS = new ArrayList<>();

        for (Feedback feedback : feedbacks) {
            // adaug fiecare feedback in lista de feedback-uri de tip DTO
            feedbackDTOS.add(FeedbackMapper.toDTO(feedback));

        }

        return feedbackDTOS;
    }

    @Override
    public void updateFeedback(UUID idFeedback, FeedbackCreationDTO feedbackCreationDTO) {
        Feedback feedback = feedbackRepository.findById(idFeedback).orElseThrow(() -> new RuntimeException("Feedback not found"));

        if(feedbackCreationDTO.getPositionX() != null) {
            feedback.setPositionX(feedbackCreationDTO.getPositionX());
        }
        if(feedbackCreationDTO.getPositionY() != null) {
            feedback.setPositionY(feedbackCreationDTO.getPositionY());
        }
        if(feedbackCreationDTO.getNoteText() != null) {
            feedback.setNoteText(feedbackCreationDTO.getNoteText());
            feedback.setContent(feedbackCreationDTO.getNoteText());
        }


        feedbackRepository.save(feedback);
    }

    @Override
    public void deleteFeedback(UUID idFeedback) {
        feedbackRepository.deleteById(idFeedback);
    }
}
