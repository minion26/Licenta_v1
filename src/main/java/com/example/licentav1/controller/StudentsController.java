package com.example.licentav1.controller;

import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import com.example.licentav1.service.StudentsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentsController {
    private final StudentsService studentsService;

    public StudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("/students")
    public Iterable<Students> getStudents() {
        return studentsService.getStudents();
    }

    @PostMapping("/create/students")
    public void addStudent(@RequestBody StudentsCreationDTO studentsCreationDTO) {
        studentsService.addStudent(studentsCreationDTO);
    }
}
