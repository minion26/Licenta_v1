package com.example.licentav1.service;

import com.example.licentav1.dto.QuestionsExamDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionsExamService {

    List<QuestionsExamDTO> getAllQuestionsForExam(UUID idExam);
}
