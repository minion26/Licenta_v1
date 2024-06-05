package com.example.licentav1.service;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CorrectAnswersExamService {
    void createCorrectAnswersExam(UUID idQuestion, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO);

    void createListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO);

    List<CorrectAnswersExamDTO> getAllCorrectAnswersExam();

    List<CorrectAnswersExamDTO> getAllCorrectAnswersExamByExam(UUID idExam);

    void deleteCorrectAnswersExam(UUID idAnswer);

    void updateCorrectAnswersExam(UUID idAnswer, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO);

    void updateListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO);

    CorrectAnswersExamDTO getCorrectAnswersExamByQuestion(UUID idQuestion);
}
