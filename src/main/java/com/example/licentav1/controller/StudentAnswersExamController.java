package com.example.licentav1.controller;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.ReviewStudentAnswersDTO;
import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import com.example.licentav1.service.StudentAnswersExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/student-answers")
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

    @GetMapping("/idExam={idExam}/idStudent={idStudent}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentAnswersExamCreationDTO> getStudentAnswers(@PathVariable UUID idExam, @PathVariable UUID idStudent) {
        return studentAnswersExamService.getStudentAnswers(idExam, idStudent);
    }

    @GetMapping("/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentAnswersExamCreationDTO> getAllStudentsAnswers(@PathVariable UUID idExam) {
        return studentAnswersExamService.getAllStudentsAnswers(idExam);
    }

    @GetMapping("/needs-review")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewStudentAnswersDTO> getStudentsAnswersForReview() {
        return studentAnswersExamService.getStudentsAnswersForReview();
    }

    @PostMapping("/reviewed/idStudentAnswerExam={idStudentAnswerExam}")
    @ResponseStatus(HttpStatus.OK)
    public void setReviewed(@PathVariable UUID idStudentAnswerExam, @RequestBody CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        studentAnswersExamService.setReviewed(idStudentAnswerExam, correctAnswersExamCreationDTO);
    }


}
