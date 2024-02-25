package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.StudentCourseRelationNotFoundException;
import com.example.licentav1.dto.StudentsFollowCoursesDTO;
import com.example.licentav1.service.StudentsFollowCoursesService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1/students-follow-courses")
public class StudentsFollowCoursesController {

    private final StudentsFollowCoursesService studentsFollowCoursesService;

    public StudentsFollowCoursesController(StudentsFollowCoursesService studentsFollowCoursesService) {
        this.studentsFollowCoursesService = studentsFollowCoursesService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        studentsFollowCoursesService.uploadFile(file);
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentsFollowCoursesDTO> getAllStudentsFollowCourses() {
        return studentsFollowCoursesService.getAllStudentsFollowCourses();
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteStudentFollowCourse(@PathVariable("id") String id) throws StudentCourseRelationNotFoundException {
        studentsFollowCoursesService.deleteStudentFollowCourse(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateStudentFollowCourse(@PathVariable("id") String id, @RequestBody StudentsFollowCoursesDTO studentsFollowCoursesDTO) throws StudentCourseRelationNotFoundException {
        studentsFollowCoursesService.updateStudentFollowCourse(id, studentsFollowCoursesDTO);
    }
}
