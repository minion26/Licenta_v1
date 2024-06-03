package com.example.licentav1.controller;

import com.example.licentav1.dto.QuestionDTO;
import com.example.licentav1.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/question")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/all/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionDTO> getAllQuestionsByExam(@PathVariable UUID idExam) {
        return questionService.getAllQuestionsByExam(idExam);
    }

    @PostMapping("/create/question/idExam={idExam}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createQuestion(@RequestBody List<QuestionDTO> questionsDTO, @PathVariable UUID idExam) {
        questionService.createQuestion(questionsDTO, idExam);
    }

    @DeleteMapping("/delete/idQuestion={idQuestion}/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteQuestion(@PathVariable UUID idQuestion, @PathVariable UUID idExam) {
        questionService.deleteQuestion(idQuestion, idExam);
    }
}
