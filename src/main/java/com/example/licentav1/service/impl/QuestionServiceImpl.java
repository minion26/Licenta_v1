package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.QuestionDTO;
import com.example.licentav1.mapper.QuestionMapper;
import com.example.licentav1.mapper.QuestionsExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final QuestionsExamRepository questionsExamRepository;
    private final CorrectAnswersExamRepository correctAnswersExamRepository;
    private final StudentAnswersExamRepository studentAnswersExamRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository, QuestionsExamRepository questionsExamRepository, CorrectAnswersExamRepository correctAnswersExamRepository, StudentAnswersExamRepository studentAnswersExamRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.studentAnswersExamRepository = studentAnswersExamRepository;
    }

    @Override
    public List<QuestionDTO> getAllQuestionsByExam(UUID idExam) {
        List<CorrectAnswersExam> correctAnswers = new ArrayList<>();

        List<QuestionDTO> questionDTOList = new ArrayList<>();

        List<Question> questions = questionRepository.getAllQuestionsByExam(idExam);

        for(Question question : questions){
            QuestionsExam questionsExam = questionsExamRepository.findByIdQuestionAndIdExam(question.getIdQuestion(), idExam).orElseThrow(() -> new ExamNotFoundException("Question not found"));
            UUID idQuestionExam = questionsExam.getIdQuestionsExam();
            CorrectAnswersExam correctAnswer = correctAnswersExamRepository.findByIdQuestionExam(idQuestionExam).orElse(null);

            if(correctAnswer != null){
                correctAnswers.add(correctAnswer);
                questionDTOList.add(QuestionMapper.toDTO(question, Collections.singletonList(correctAnswer)));
            }else{
                //daca e null las lista goala
                questionDTOList.add(QuestionMapper.toDTO(question, Collections.emptyList()));
            }

        }

        return questionDTOList;


//        return questionRepository.getAllQuestionsByExam(idExam).stream()
//                .map(question -> {
//                    // am id exam, imi trebuie id urile intrebarilor ca sa iau id_question_exam si cu el iua raspunsul
//                    UUID idQuestion = question.getIdQuestion();
//                    System.out.println("idQuestion: " + idQuestion);
//                    QuestionsExam questionsExam = questionsExamRepository.findByIdQuestionAndIdExam(idQuestion, idExam).orElseThrow(() -> new ExamNotFoundException("Question not found"));
//                    UUID idQuestionExam = questionsExam.getIdQuestionsExam();
//                    System.out.println("idQuestionExam: " + idQuestionExam);
//                    CorrectAnswersExam correctAnswer = correctAnswersExamRepository.findByIdQuestionExam(idQuestionExam).orElseThrow(() -> new ExamNotFoundException("Correct answer not found"));
//                    correctAnswers.add(correctAnswer);
//                    System.out.println("correctAnswer: " + correctAnswer.getCorrectAnswer());
//
//                    return QuestionMapper.toDTO(question, correctAnswers);
//                })
//                .collect(Collectors.toList());
    }

    @Override
    public void createQuestion(List<QuestionDTO> questionsDTO, UUID idExam) {
        //gasesc examenul
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //pentru fiecare intrebare
        for(QuestionDTO questionDTO : questionsDTO){
            //creez intrebarea
            Question question = QuestionMapper.fromDTO(questionDTO, exam);

            //adaug intrebarea la lista de intrebari a examenului
            exam.getQuestionsList().add(question);

            //salvez intrebarea
            questionRepository.save(question);

            //salvez in questions-exam
            questionsExamRepository.save(QuestionsExamMapper.fromDTO(exam, question));

        }

        examRepository.save(exam);



//        Question question = QuestionMapper.fromDTO(questionDTO, exam);
//
//        exam.getQuestionsList().add(question);
//        examRepository.save(exam);
//
//        // save to questions-exam table
//
//        questionRepository.save(question);
//
//        questionsExamRepository.save(QuestionsExamMapper.fromDTO(exam, question));

    }

    @Override
    public void deleteQuestion(UUID idQuestion, UUID idExam) {
        // find the exam
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //list all the questions
        List<Question> questions = exam.getQuestionsList();

        //find the question to delete
        Question question = questions.stream()
                .filter(q -> q.getIdQuestion().equals(idQuestion))
                .findFirst()
                .orElseThrow(() -> new ExamNotFoundException("Question not found"));

        //delete the answers for that question if it has one
        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(question.getIdQuestion()).orElse(null);
        if (correctAnswersExam != null) {
            correctAnswersExamRepository.delete(correctAnswersExam);
        }

        //delete the question from the exam
        questions.remove(question);


        QuestionsExam questionsExam = questionsExamRepository.findByIdQuestionAndIdExam(idQuestion, idExam).orElseThrow(() -> new ExamNotFoundException("Question not found"));
        if (questionsExam != null) {
            System.out.println("Question found");

            StudentAnswersExam studentAnswersExam =  studentAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElse(null);
            if(studentAnswersExam != null){
                studentAnswersExamRepository.delete(studentAnswersExam);
            }

            //delete the question from the questions-exam table
            questionsExamRepository.delete(questionsExam);

            //delete the question from the questions table
            questionRepository.delete(question);
        }else{
            System.out.println("Question not found");
        }


        //save the exam
        examRepository.save(exam);
    }
}
