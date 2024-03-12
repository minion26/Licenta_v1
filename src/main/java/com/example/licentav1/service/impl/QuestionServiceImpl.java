package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.QuestionDTO;
import com.example.licentav1.mapper.QuestionMapper;
import com.example.licentav1.mapper.QuestionsExamMapper;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.QuestionRepository;
import com.example.licentav1.repository.QuestionsExamRepository;
import com.example.licentav1.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private QuestionsExamRepository questionsExamRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository, QuestionsExamRepository questionsExamRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.questionsExamRepository = questionsExamRepository;
    }

    @Override
    public List<QuestionDTO> getAllQuestionsByExam(UUID idExam) {
        return questionRepository.getAllQuestionsByExam(idExam).stream()
                .map(QuestionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createQuestion(QuestionDTO questionDTO, UUID idExam) {
        //gasesc examenul
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        Question question = QuestionMapper.fromDTO(questionDTO, exam);

        exam.getQuestionsList().add(question);
        examRepository.save(exam);

        // save to questions-exam table

        questionRepository.save(question);

        questionsExamRepository.save(QuestionsExamMapper.fromDTO(exam, question));

    }
}
