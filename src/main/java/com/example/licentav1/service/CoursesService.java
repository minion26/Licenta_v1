package com.example.licentav1.service;

import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface CoursesService {

    void createCourse(CoursesCreationDTO coursesCreationDTO);

    List<CoursesDTO> getCourses();

    void uploadCourses(MultipartFile file) throws IOException;

    void deleteCourse(UUID id);

    void updateCourse(UUID id, CoursesDTO coursesDTO);

    CoursesDTO getCourseById(UUID id);

    List<CoursesDTO> getCoursesByTeacher(UUID idTeacher);
}
