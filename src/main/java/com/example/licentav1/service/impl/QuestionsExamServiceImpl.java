package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.QuestionsExamDTO;
import com.example.licentav1.mapper.QuestionsExamMapper;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.QuestionRepository;
import com.example.licentav1.repository.QuestionsExamRepository;
import com.example.licentav1.service.QuestionsExamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionsExamServiceImpl implements QuestionsExamService {
    private final QuestionsExamRepository questionsExamRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public QuestionsExamServiceImpl(QuestionsExamRepository questionsExamRepository, QuestionRepository questionRepository, ExamRepository examRepository) {
        this.questionsExamRepository = questionsExamRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }


    @Override
    public List<QuestionsExamDTO> getAllQuestionsForExam(UUID idExam) {
        // caut toate înregistrările care au idExam în questionsExamRepository
        List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(idExam);

        // transform fiecare QuestionsExam în QuestionsExamDTO și le adaug într-o listă
        return questionsExams.stream()
                .map(QuestionsExamMapper::toDTO)
                .collect(Collectors.toList());
    }
}
