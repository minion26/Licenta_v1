package com.example.licentav1.controller;

import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.service.ExamService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/exam")
public class ExamController {
    private ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping("/create/idCourse={idCourse}&idTeacher={idTeacher}")
    public void createExam(@RequestBody ExamCreationDTO examCreationDTO, @PathVariable UUID idCourse, @PathVariable UUID idTeacher) {
        examService.createExam(examCreationDTO, idCourse, idTeacher);
    }
}
