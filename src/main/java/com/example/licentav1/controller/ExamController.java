package com.example.licentav1.controller;

import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.dto.ExamDTO;
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
}
