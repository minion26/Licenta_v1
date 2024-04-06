package com.example.licentav1.controller;

import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import com.example.licentav1.service.StudentAnswersExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/student-correct-answers")
public class StudentAnswersExamController {
    private final StudentAnswersExamService studentAnswersExamService;

    public StudentAnswersExamController(StudentAnswersExamService studentAnswersExamService) {
        this.studentAnswersExamService = studentAnswersExamService;
    }

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public void submitExamAnswers(@RequestBody StudentAnswersExamCreationDTO studentAnswersExamCreationDTO) {
        studentAnswersExamService.submitExamAnswers(studentAnswersExamCreationDTO);
    }

    @DeleteMapping("/delete/idExam={idExam}/idStudent={idStudent}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudentAnswers(@PathVariable UUID idExam, @PathVariable UUID idStudent) {
        studentAnswersExamService.deleteStudentAnswers(idExam, idStudent);
    }



}
