package com.example.licentav1.controller;

import com.example.licentav1.dto.QuestionsExamDTO;
import com.example.licentav1.service.QuestionsExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/question-exam")
public class QuestionsExamController {
    private final QuestionsExamService questionExamService;

    public QuestionsExamController(QuestionsExamService questionExamService) {
        this.questionExamService = questionExamService;
    }

    @GetMapping("/all/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionsExamDTO> getAllQuestionsForExam(@PathVariable UUID idExam) {
        try {
            return questionExamService.getAllQuestionsForExam(idExam);
        } catch (Exception e) {
            // handle exception and send error response here
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred", e);
        }
    }

}
