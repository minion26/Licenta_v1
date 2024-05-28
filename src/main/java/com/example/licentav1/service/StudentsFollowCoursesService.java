package com.example.licentav1.service;

import com.example.licentav1.dto.StudentsFollowCoursesDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface StudentsFollowCoursesService {
    void uploadFile(MultipartFile file) throws IOException;

    List<StudentsFollowCoursesDTO> getAllStudentsFollowCourses();

    void deleteStudentFollowCourse(String id);

    void updateStudentFollowCourse(String id, StudentsFollowCoursesDTO studentsFollowCoursesDTO);

    List<StudentsFollowCoursesDTO> getStudentFollowCourse(String courseName);
}
