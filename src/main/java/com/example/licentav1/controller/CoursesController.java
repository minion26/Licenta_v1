package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.service.CoursesService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.lang.IllegalArgumentException;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CoursesDTO getCourseById(@PathVariable("id") UUID id) throws CourseNotFoundException {
        return coursesService.getCourseById(id);
    }

    @GetMapping("/idTeacher={idTeacher}")
    @ResponseStatus(HttpStatus.OK)
    public List<CoursesDTO> getCoursesByTeacher(@PathVariable("idTeacher") UUID idTeacher) {
        return coursesService.getCoursesByTeacher(idTeacher);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCourse( @RequestBody @Valid CoursesCreationDTO coursesCreationDTO) throws CourseAlreadyExistsException {
        if (coursesCreationDTO == null) {
            throw new IllegalArgumentException("CoursesCreationDTO object cannot be null");
        }

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
    public void updateCourse(@PathVariable("id") UUID id, @RequestBody CoursesDTO coursesDTO) throws CourseNotFoundException {
        coursesService.updateCourse(id, coursesDTO);
    }

    @GetMapping("/get-teachers/idCourse={idCourse}")
    @ResponseStatus(HttpStatus.OK)
    public List<TeachersDTO> getTeachersByCourse(@PathVariable UUID idCourse) {
        return coursesService.getTeachersByCourse(idCourse);
    }

    @GetMapping("/get-courses-for-student/idStudent={idStudent}/semester={semester}")
    @ResponseStatus(HttpStatus.OK)
    public List<CoursesDTO> getCoursesForStudent(@PathVariable UUID idStudent, @PathVariable Integer semester) {
        return coursesService.getCoursesForStudent(idStudent, semester);
    }

}
