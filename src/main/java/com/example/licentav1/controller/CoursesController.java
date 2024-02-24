package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.service.CoursesService;
import com.example.licentav1.service.DidacticService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadCourses(@RequestParam("file") MultipartFile file) throws IOException, CourseAlreadyExistsException{
        coursesService.uploadCourses(file);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable("id") UUID id) throws CourseNotFoundException {
        coursesService.deleteCourse(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateCourse(@PathVariable("id") UUID id, @RequestBody CoursesCreationDTO coursesCreationDTO) throws CourseNotFoundException {
        coursesService.updateCourse(id, coursesCreationDTO);
    }

}
