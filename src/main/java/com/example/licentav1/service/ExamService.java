package com.example.licentav1.service;

import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.dto.ExamDTO;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    void createExam(ExamCreationDTO examCreationDTO, UUID idCourse);

    List<ExamDTO> getAllExams();
}
