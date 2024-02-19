package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.TeacherAlreadyExistsException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.service.TeachersService;
import com.example.licentav1.domain.Teachers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/teachers")
public class TeachersController {

    private final TeachersService teachersService;

    public TeachersController(TeachersService teachersService) {
        this.teachersService = teachersService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<TeachersDTO> getTeachers() {
        return teachersService.getTeachers();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTeacher(@RequestBody TeachersCreationDTO teachersCreationDTO) throws UserAlreadyExistsException, TeacherAlreadyExistsException {
        teachersService.createTeacher(teachersCreationDTO);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeacher(@PathVariable(value="id") UUID id, @RequestBody TeachersDTO teachersDTO) throws UserNotFoundException, TeacherNotFoundException {
        teachersService.updateTeacher(id, teachersDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeacher(@PathVariable(value="id") UUID id) throws UserNotFoundException, TeacherNotFoundException{
        teachersService.deleteTeacher(id);
    }
}
