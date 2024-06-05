package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.CorrectAnswersExamCreationDTO;
import com.example.licentav1.dto.CorrectAnswersExamDTO;
import com.example.licentav1.mapper.CorrectAnswersExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.CorrectAnswersExamService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final TeachersRepository teachersRepository;
    private final DidacticRepository didacticRepository;


    public CorrectAnswersExamServiceImpl(CorrectAnswersExamRepository correctAnswersExamRepository, QuestionsExamRepository questionsExamRepository, QuestionRepository questionRepository, ExamRepository examRepository, HttpServletRequest request, JwtService jwtService, TeachersRepository teachersRepository, DidacticRepository didacticRepository) {
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.request = request;
        this.jwtService = jwtService;
        this.teachersRepository = teachersRepository;
        this.didacticRepository = didacticRepository;
    }

    @Override
    public void createCorrectAnswersExam(UUID idQuestion, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + id);

        //am profesorul care a facut request-ul
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //Question question = questionRepository.findById(idQuestion).orElseThrow(() -> new QuestionNotFound("Question not found"));

        QuestionsExam questionsExam = questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));

        //am question-exam iau examenul
        Exam exam = examRepository.findById(questionsExam.getExam().getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //am examenul, iau cursul
        Courses course = exam.getCourse();

        //verific daca profesorul preda la cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }

        // Check if a correct answer already exists for the given question
        CorrectAnswersExam existingCorrectAnswer = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElse(null);
        if (existingCorrectAnswer != null) {
            throw new CorrectAnswerAlreadyExistsException("A correct answer already exists for this question");
        }

        CorrectAnswersExam correctAnswersExam = CorrectAnswersExamMapper.fromDTO(correctAnswersExamCreationDTO, questionsExam);

        correctAnswersExamRepository.save(correctAnswersExam);

    }

    @Override
    public void createListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + id);

        //am profesorul care a facut request-ul
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //iau cursul
        Courses course = exam.getCourse();
        //verific daca profesorul preda la cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }

        //List<Question> questions = questionRepository.findAllByExam(exam);

        mapOfCorrectAnswersExamCreationDTO.forEach((idQuestion, correctAnswersExamCreationDTO) -> {
           //Question q = questionRepository.findById(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));

            QuestionsExam questionsExam = questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));

            // Check if a correct answer already exists for the given question
            CorrectAnswersExam existingCorrectAnswer = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElse(null);
            if (existingCorrectAnswer != null) {
                throw new CorrectAnswerAlreadyExistsException("A correct answer already exists for question id: " + idQuestion);
            }

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
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + id);

        //am profesorul care a facut request-ul
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        // Find the correct answer
        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findById(idAnswer).orElseThrow(() -> new AnswerNotFoundException("Answer not found"));

        //iau question-exam
        QuestionsExam questionsExam = questionsExamRepository.findById(correctAnswersExam.getQuestionsExam().getIdQuestionsExam()).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));

        //iau examenul
        Exam exam = examRepository.findById(questionsExam.getExam().getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //iau cursul
        Courses course = exam.getCourse();
        //vad daca profesorul preda la cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }


        // Update the correct answer
        if (correctAnswersExamCreationDTO.getCorrectAnswer() != null)
            correctAnswersExam.setCorrectAnswer(correctAnswersExamCreationDTO.getCorrectAnswer());
        if (correctAnswersExamCreationDTO.getScore() != null)
            correctAnswersExam.setScore(correctAnswersExamCreationDTO.getScore());

        // save it to db
        correctAnswersExamRepository.save(correctAnswersExam);
    }

    @Override
    public void updateListOfCorrectAnswersExam(UUID idExam, Map<UUID, CorrectAnswersExamCreationDTO> mapOfCorrectAnswersExamCreationDTO) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + id);

        //am profesorul care a facut request-ul
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //iau cursul
        Courses course = exam.getCourse();
        //verific daca profesorul preda la cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }

        mapOfCorrectAnswersExamCreationDTO.forEach((idQuestion, correctAnswersExamCreationDTO) -> {
            QuestionsExam questionsExam = questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElseThrow(() -> new AnswerNotFoundException("Answer not found"));

            if (correctAnswersExamCreationDTO.getCorrectAnswer() != null && !correctAnswersExam.getCorrectAnswer().equals(correctAnswersExamCreationDTO.getCorrectAnswer()))
                correctAnswersExam.setCorrectAnswer(correctAnswersExamCreationDTO.getCorrectAnswer());
            if (correctAnswersExamCreationDTO.getScore() != null && correctAnswersExam.getScore()!=correctAnswersExamCreationDTO.getScore())
                correctAnswersExam.setScore(correctAnswersExamCreationDTO.getScore());

            correctAnswersExamRepository.save(correctAnswersExam);
        });
    }

    @Override
    public CorrectAnswersExamDTO getCorrectAnswersExamByQuestion(UUID idQuestion) {
        QuestionsExam questionsExam = questionsExamRepository.findByIdQuestion(idQuestion).orElseThrow(() -> new QuestionsExamNotFoundException("QuestionsExam not found"));
        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElseThrow(() -> new AnswerNotFoundException("Answer not found"));
        return CorrectAnswersExamMapper.toDTO(correctAnswersExam);
    }


}
