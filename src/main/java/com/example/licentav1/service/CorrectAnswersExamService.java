package com.example.licentav1.service;

import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CorrectAnswersExamService {
    void createCorrectAnswersExam(UUID idQuestion, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO);

    void createListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO);
}
