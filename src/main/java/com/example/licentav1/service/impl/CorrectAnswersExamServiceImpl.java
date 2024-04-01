package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.AnswerNotFoundException;
import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.advice.exceptions.QuestionsExamNotFoundException;
import com.example.licentav1.domain.CorrectAnswersExam;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import com.example.licentav1.mapper.CorrectAnswersExamMapper;
import com.example.licentav1.repository.CorrectAnswersExamRepository;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.QuestionRepository;
import com.example.licentav1.repository.QuestionsExamRepository;
import com.example.licentav1.service.CorrectAnswersExamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @Override
    public void createListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));
        //List<Question> questions = questionRepository.findAllByExam(exam);

        mapOfCorrectAnswersExamCreationDTO.forEach((idQuestion, correctAnswersExamCreationDTO) -> {
            Question q = questionRepository.findById(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));
            CorrectAnswersExam correctAnswersExam = CorrectAnswersExamMapper.fromDTO(correctAnswersExamCreationDTO, questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found")));
            correctAnswersExamRepository.save(correctAnswersExam);
        });

    }

    @Override
    public List<CorrectAnswersExamDTO> getAllCorrectAnswersExam() {
        List<CorrectAnswersExam> all = correctAnswersExamRepository.findAll();
        List<CorrectAnswersExamDTO> correctAnswersExamDTOS = new ArrayList<>();
        for (CorrectAnswersExam correctAnswersExam : all) {
            CorrectAnswersExamDTO response = CorrectAnswersExamMapper.toDTO(correctAnswersExam);
            // System.out.println(response);
            correctAnswersExamDTOS.add(response);
        }
        return correctAnswersExamDTOS;
    }

    @Override
    public List<CorrectAnswersExamDTO> getAllCorrectAnswersExamByExam(UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));
        List<QuestionsExam> questions = questionsExamRepository.findAllByIdExam(idExam);
        List<CorrectAnswersExamDTO> correctAnswersExamDTOS = new ArrayList<>();
        for (QuestionsExam question : questions) {
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(question.getIdQuestionsExam()).orElse(null);
            System.out.println(question.getIdQuestionsExam());
            if (correctAnswersExam != null) {
                CorrectAnswersExamDTO correctAnswersExamDTO = CorrectAnswersExamMapper.toDTO(correctAnswersExam);
                correctAnswersExamDTOS.add(correctAnswersExamDTO);
            }
        }
        return correctAnswersExamDTOS;

    }

    @Override
    public void deleteCorrectAnswersExam(UUID idAnswer) {
        // Find the correct answer
        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findById(idAnswer).orElseThrow(() -> new AnswerNotFoundException("Answer not found"));
        // Delete the correct answer
        correctAnswersExamRepository.delete(correctAnswersExam);

    }

    @Override
    public void updateCorrectAnswersExam(UUID idAnswer, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        // Find the correct answer
        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findById(idAnswer).orElseThrow(() -> new AnswerNotFoundException("Answer not found"));

        // Update the correct answer
        if (correctAnswersExamCreationDTO.getCorrectAnswer() != null)
            correctAnswersExam.setCorrectAnswer(correctAnswersExamCreationDTO.getCorrectAnswer());
        if (correctAnswersExamCreationDTO.getScore() != null)
            correctAnswersExam.setScore(correctAnswersExamCreationDTO.getScore());

        // save it to db
        correctAnswersExamRepository.save(correctAnswersExam);
    }


}
