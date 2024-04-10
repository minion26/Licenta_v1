package com.example.licentav1.service;

import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import com.example.licentav1.dto.ReviewStudentAnswersDTO;

import java.util.List;
import java.util.UUID;

public interface StudentAnswersExamService {
    void submitExamAnswers(StudentAnswersExamCreationDTO studentAnswersExamCreationDTO);

    void deleteStudentAnswers(UUID idExam, UUID idStudent);

    List<StudentAnswersExamCreationDTO> getStudentAnswers(UUID idExam, UUID idStudent);

    List<StudentAnswersExamCreationDTO> getAllStudentsAnswers(UUID idExam);

    List<ReviewStudentAnswersDTO> getStudentsAnswersForReview();
}
