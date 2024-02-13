package com.example.licentav1.controller;

import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.service.TeachersService;
import com.example.licentav1.domain.Teachers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TeachersController {

    private final TeachersService teachersService;

    public TeachersController(TeachersService teachersService) {
        this.teachersService = teachersService;
    }

    @GetMapping("/teachers")
    public Iterable<Teachers> getTeachers() {
        return teachersService.getTeachers();
    }

    @PostMapping("/create/teachers")
    public void createTeacher(@RequestBody TeachersCreationDTO teachersCreationDTO) {
        teachersService.createTeacher(teachersCreationDTO);
    }


}
