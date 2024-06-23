package com.example.licentav1.controller;

import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.dto.ExamDTO;
import com.example.licentav1.dto.QuestionDTO;
import com.example.licentav1.dto.StudentExamFrontDTO;
import com.example.licentav1.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/exam")
public class ExamController {
    private ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamDTO> getAllExams() {
        return examService.getAllExams();
    }

    @PostMapping("/create/idCourse={idCourse}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createExam(@RequestBody ExamCreationDTO examCreationDTO, @PathVariable UUID idCourse) {
        examService.createExam(examCreationDTO, idCourse);
    }

    @DeleteMapping("/delete/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteExam(@PathVariable UUID idExam) {
        examService.deleteExam(idExam);
    }

    @PatchMapping("/update/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public void updateExam(@RequestBody ExamDTO examDTO, @PathVariable UUID idExam) {
        examService.updateExam(examDTO, idExam);
    }

    @GetMapping("/get/idCourse={idCourse}")
    @ResponseStatus(HttpStatus.OK)
    public List<ExamDTO> getExamsByCourse(@PathVariable UUID idCourse) {
        return examService.getExamsByCourse(idCourse);
    }

    @GetMapping("/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public ExamDTO getExamById(@PathVariable UUID idExam) {
        return examService.getExamById(idExam);
    }

    @GetMapping("/get-students/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentExamFrontDTO> getStudentsByExam(@PathVariable UUID idExam) {
        return examService.getStudentsByExam(idExam);
    }

    @GetMapping("/get-questions-and-answers/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<QuestionDTO> getQuestionsAndAnswersByExam(@PathVariable UUID idExam) {
        return examService.getQuestionsAndAnswersByExam(idExam);
    }

    @PostMapping("/start-exam/idExam={idExam}")
    @ResponseStatus(HttpStatus.CREATED)
    public void startExam(@PathVariable UUID idExam) {
        examService.startExam(idExam);
    }

    @GetMapping("/is-started/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isExamStarted(@PathVariable UUID idExam) {
        return examService.isExamStarted(idExam);
    }
}
