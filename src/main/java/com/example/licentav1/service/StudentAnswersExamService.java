package com.example.licentav1.service;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.QuestionAndStudentsAnswersDTO;
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

    void setReviewed(UUID idStudentAnswerExam,CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO);

    ReviewStudentAnswersDTO getStudentAnswerForReview(UUID idStudentAnswerExam);

    List<QuestionAndStudentsAnswersDTO> getStudentsAnswers(UUID idExam, UUID idStudent);

    List<QuestionAndStudentsAnswersDTO> getMyAnswers(UUID idExam);
}
