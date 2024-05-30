package com.example.licentav1.service;

import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.dto.LecturesDTO;

import java.util.List;
import java.util.UUID;

public interface LecturesService {
    List<LecturesDTO> getLectures();
    void createLecture(LecturesCreationDTO lecturesCreationDTO, UUID idCourse);

    void deleteLecture(UUID idLecture);

    void updateLecture(LecturesDTO lecturesDTO, UUID idLecture);

    LecturesDTO getLecture(UUID idLecture);

    List<LecturesDTO> getLecturesByCourse(UUID idCourses);
}
