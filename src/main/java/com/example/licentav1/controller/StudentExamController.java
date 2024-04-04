package com.example.licentav1.controller;

import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import com.example.licentav1.service.StudentExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/student-exam")
public class StudentExamController {
    private final StudentExamService studentExamService;

    public StudentExamController(StudentExamService studentExamService) {
        this.studentExamService = studentExamService;
    }

    @GetMapping("/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentExamDTO> getAllStudentByExams(@PathVariable UUID idExam) {
        return studentExamService.getAllStudentByExam(idExam);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    // se creaza un entry pentru tabela student_exam
    public void createStudentExam(@RequestBody StudentExamCreationDTO studentExamCreationDTO) {
        studentExamService.createStudentExam(studentExamCreationDTO);
    }



}
