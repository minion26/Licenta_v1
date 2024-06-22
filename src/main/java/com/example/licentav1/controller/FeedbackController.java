package com.example.licentav1.controller;

import com.example.licentav1.dto.FeedbackCreationDTO;
import com.example.licentav1.dto.FeedbackDTO;
import com.example.licentav1.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/create/idHomework={idHomework}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createFeedback(@PathVariable("idHomework") UUID idHomework, @RequestBody List<FeedbackCreationDTO> listFeedbackCreationDTO) {
        feedbackService.createFeedback(idHomework, listFeedbackCreationDTO);
    }

    @GetMapping("/all/idHomework={idHomework}")
    @ResponseStatus(HttpStatus.OK)
    public List<FeedbackDTO> getFeedbacks(@PathVariable("idHomework") UUID idHomework) {
        return feedbackService.getFeedbacks(idHomework);
    }

    @PatchMapping("/update/idFeedback={idFeedback}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFeedback(@PathVariable("idFeedback") UUID idFeedback, @RequestBody FeedbackCreationDTO feedbackCreationDTO) {
        feedbackService.updateFeedback(idFeedback, feedbackCreationDTO);
    }

    @DeleteMapping("/delete/idFeedback={idFeedback}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFeedback(@PathVariable("idFeedback") UUID idFeedback) {
        feedbackService.deleteFeedback(idFeedback);
    }

    @PostMapping("/createOrUpdate/idHomework={idHomework}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrUpdateFeedback(@PathVariable("idHomework") UUID idHomework, @RequestBody List<FeedbackCreationDTO> listFeedbackCreationDTO) {
        feedbackService.createOrUpdateFeedback(idHomework, listFeedbackCreationDTO);
    }
}
