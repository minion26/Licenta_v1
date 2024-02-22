package com.example.licentav1.service;

import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;

import java.util.List;

public interface CoursesService {

    void createCourse(CoursesCreationDTO coursesCreationDTO);

    List<CoursesDTO> getCourses();
}
