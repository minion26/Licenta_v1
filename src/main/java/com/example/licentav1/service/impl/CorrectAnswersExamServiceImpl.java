package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.QuestionsExamNotFoundException;
import com.example.licentav1.domain.CorrectAnswersExam;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.mapper.CorrectAnswersExamMapper;
import com.example.licentav1.repository.CorrectAnswersExamRepository;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.QuestionRepository;
import com.example.licentav1.repository.QuestionsExamRepository;
import com.example.licentav1.service.CorrectAnswersExamService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CorrectAnswersExamServiceImpl implements CorrectAnswersExamService {
    private final CorrectAnswersExamRepository correctAnswersExamRepository;
    private final QuestionsExamRepository questionsExamRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public CorrectAnswersExamServiceImpl(CorrectAnswersExamRepository correctAnswersExamRepository, QuestionsExamRepository questionsExamRepository, QuestionRepository questionRepository, ExamRepository examRepository) {
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }

    @Override
    public void createCorrectAnswersExam(UUID idQuestion, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        //Question question = questionRepository.findById(idQuestion).orElseThrow(() -> new QuestionNotFound("Question not found"));

        QuestionsExam questionsExam = questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));

        CorrectAnswersExam correctAnswersExam = CorrectAnswersExamMapper.fromDTO(correctAnswersExamCreationDTO, questionsExam);

        correctAnswersExamRepository.save(correctAnswersExam);

    }



}