package com.example.licentav1.controller;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import com.example.licentav1.service.CorrectAnswersExamService;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CorrectAnswersExamDTO> getAllCorrectAnswersExam() {
        return correctAnswersExamService.getAllCorrectAnswersExam();
    }

    @GetMapping("all/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<CorrectAnswersExamDTO> getAllCorrectAnswersExamByExam(@PathVariable UUID idExam) {
        return correctAnswersExamService.getAllCorrectAnswersExamByExam(idExam);
    }


    @PostMapping("/create/idQuestion={idQuestion}")
    @ResponseStatus(HttpStatus.CREATED)
    // for one question, create one correct answer
    public void createCorrectAnswersExam(@PathVariable UUID idQuestion, @RequestBody CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        correctAnswersExamService.createCorrectAnswersExam(idQuestion, correctAnswersExamCreationDTO);
    }

    @PostMapping("/createByExam/idExam={idExam}")
    @ResponseStatus(HttpStatus.CREATED)
    // for one exam, for multiple questions, create correct answers
    public void createCorrectAnswersExam(@PathVariable UUID idExam, @RequestBody Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO) {
        correctAnswersExamService.createListOfCorrectAnswersExam(idExam, mapOfCorrectAnswersExamCreationDTO);
    }

    @DeleteMapping("/delete/idAnswer={idAnswer}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCorrectAnswersExam(@PathVariable UUID idAnswer) {
        correctAnswersExamService.deleteCorrectAnswersExam(idAnswer);
    }

    @PatchMapping("/update/idAnswer={idAnswer}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCorrectAnswersExam(@PathVariable UUID idAnswer, @RequestBody CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        correctAnswersExamService.updateCorrectAnswersExam(idAnswer, correctAnswersExamCreationDTO);
    }
}
