package com.example.licentav1.controller;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import com.example.licentav1.service.CorrectAnswersExamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/correct-answers-exam")
public class CorrectAnswersExamController {
    private final CorrectAnswersExamService correctAnswersExamService;

    public CorrectAnswersExamController(CorrectAnswersExamService correctAnswersExamService) {
        this.correctAnswersExamService = correctAnswersExamService;
    }

    @PostMapping("/create/idQuestion={idQuestion}")
    public void createCorrectAnswersExam(@PathVariable UUID idQuestion, @RequestBody CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        correctAnswersExamService.createCorrectAnswersExam(idQuestion, correctAnswersExamCreationDTO);
    }

    @PostMapping("/createByExam/idExam={idExam}")
    public void createCorrectAnswersExam(@PathVariable UUID idExam, @RequestBody Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO) {
        correctAnswersExamService.createListOfCorrectAnswersExam(idExam, mapOfCorrectAnswersExamCreationDTO);
    }


}
