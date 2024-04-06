package com.example.licentav1.service;

import com.example.licentav1.dto.StudentAnswersExamCreationDTO;

import java.util.UUID;

public interface StudentAnswersExamService {
    void submitExamAnswers(StudentAnswersExamCreationDTO studentAnswersExamCreationDTO);

    void deleteStudentAnswers(UUID idExam, UUID idStudent);
}
