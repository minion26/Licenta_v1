package com.example.licentav1.service;

import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.LecturesCreationDTO;

import java.util.List;
import java.util.UUID;

public interface LecturesService {
    List<Lectures> getLectures();
    void createLecture(LecturesCreationDTO lecturesCreationDTO, UUID idCourse);
}
