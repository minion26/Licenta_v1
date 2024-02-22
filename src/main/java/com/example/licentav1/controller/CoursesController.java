package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.service.CoursesService;
import com.example.licentav1.service.DidacticService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/courses")
public class CoursesController {
    private final CoursesService coursesService;

    public CoursesController(CoursesService coursesService) {
        this.coursesService = coursesService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<CoursesDTO> getCourses() {
        return coursesService.getCourses();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCourse(@RequestBody CoursesCreationDTO coursesCreationDTO) throws CourseAlreadyExistsException {
        coursesService.createCourse(coursesCreationDTO);
    }
}
