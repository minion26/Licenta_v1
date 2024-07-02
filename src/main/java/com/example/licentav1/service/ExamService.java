package com.example.licentav1.service;

import com.example.licentav1.dto.*;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    void createExam(ExamCreationDTO examCreationDTO, UUID idCourse);

    List<ExamDTO> getAllExams();

    void deleteExam(UUID idExam);

    void updateExam(ExamDTO examCreationDTO, UUID idExam);

    List<ExamDTO> getExamsByCourse(UUID idCourse);

    ExamDTO getExamById(UUID idExam);

    List<StudentExamFrontDTO> getStudentsByExam(UUID idExam);

    List<QuestionDTO> getQuestionsAndAnswersByExam(UUID idExam);

    void startExam(UUID idExam);

    boolean isExamStarted(UUID idExam);

    ExamExtraDetailsDTO getExamExtraDetails(UUID idExam);
}
