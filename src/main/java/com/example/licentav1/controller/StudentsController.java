package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.StudentAlreadyExistsException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import com.example.licentav1.service.StudentsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/students")
public class StudentsController {
    private final StudentsService studentsService;

    public StudentsController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<StudentsDTO> getStudents() {
        return studentsService.getStudents();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStudent(@RequestBody StudentsCreationDTO studentsCreationDTO) throws StudentAlreadyExistsException, UserAlreadyExistsException {
        studentsService.createStudent(studentsCreationDTO);

    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadStudents(@RequestParam("file")MultipartFile file) throws IOException, StudentAlreadyExistsException, UserAlreadyExistsException{
        studentsService.uploadStudents(file);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStudent(@PathVariable(value="id") UUID id, @RequestBody StudentsDTO studentsDTO) throws UserNotFoundException, StudentNotFoundException {
        studentsService.updateStudent(id, studentsDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable(value="id") UUID id) throws UserNotFoundException, StudentNotFoundException {
        studentsService.deleteStudent(id);
    }
}
