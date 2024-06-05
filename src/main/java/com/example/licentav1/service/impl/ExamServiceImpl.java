package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.*;
import com.example.licentav1.mapper.*;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.ExamService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ExamServiceImpl implements ExamService {

    private final DidacticRepository didacticRepository;
    private final HttpServletRequest request;
    private final JwtService jwtService;

    private final ExamRepository examRepository;
    private final CoursesRepository coursesRepository;
    private final QuestionRepository questionRepository;
    private final QuestionsExamRepository questionsExamRepository;
    private final TeacherExamRepository teacherExamRepository;
    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;
    private final StudentExamRepository studentExamRepository;
    private final CorrectAnswersExamRepository correctAnswersExamRepository;

    public ExamServiceImpl(JwtService jwtService, ExamRepository examRepository, CoursesRepository coursesRepository, QuestionRepository questionRepository, QuestionsExamRepository questionsExamRepository, TeacherExamRepository teacherExamRepository, TeachersRepository teachersRepository, UsersRepository usersRepository, StudentExamRepository studentExamRepository, CorrectAnswersExamRepository correctAnswersExamRepository, DidacticRepository didacticRepository, HttpServletRequest request) {
        this.jwtService = jwtService;
        this.examRepository = examRepository;
        this.coursesRepository = coursesRepository;
        this.questionRepository = questionRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.teacherExamRepository = teacherExamRepository;
        this.teachersRepository = teachersRepository;
        this.usersRepository = usersRepository;
        this.studentExamRepository = studentExamRepository;
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.didacticRepository = didacticRepository;
        this.request = request;
    }

    @Override
    public void createExam(ExamCreationDTO examCreationDTO, UUID idCourse) {
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

        UUID idToken = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + idToken);

        //am profesorul care a facut request-ul
        Teachers teacherFromJwt = teachersRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //gasesc cursul pentru a asocia examenul
        Courses courses = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), courses.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to create this exam");
        }else{
            System.out.println("You are allowed to create this exam");
        }

        //gasesc profesorii pentru a asocia examenul
        List<Teachers> teachers = new ArrayList<>();
        for (UUID id: examCreationDTO.getIdTeacher()) {
            Teachers t = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
            if (t != null)
                teachers.add(t);
        }

        // salvez examenul: nume, timp, scor total, scor de trecere, data, curs
        Exam exam =  examRepository.save(ExamMapper.fromDTO(examCreationDTO, courses));

        // salvez intrebarile
        List<Question> questions = new ArrayList<>();
        for (QuestionCreationDTO questionDTO : examCreationDTO.getQuestion()) {
            // salvez intrebarea
            Question question = new Question();
            if(questionDTO.getIdQuestion() != null) // e null cand creez examenul si nu am id ca el e generat automat in questions entity
                question.setIdQuestion(questionDTO.getIdQuestion());

            question.setQuestionText(questionDTO.getQuestionText());
            question.setExam(exam);
            questions.add(question);
            //o pun in tabel cu examenul
            questionRepository.save(question);

        }

        exam.setQuestionsList(questions);
        examRepository.save(exam);

        //salvez relatia intre examen si cati profesori sunt
        for(Teachers t: teachers){
            teacherExamRepository.save(TeacherExamMapper.fromDTO(exam, t));
        }

        //salvez relatia intre intrebari si examen
        for(Question q: questions){
            questionsExamRepository.save(QuestionsExamMapper.fromDTO(exam, q));
        }


    }

    @Override
    public List<ExamDTO> getAllExams() {
        return examRepository.findAll().stream().map(
                exam -> {
                    ExamDTO examDTO = ExamMapper.toDTO(exam);

                    //gasesc intrebarile pentru examen
                    List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
                    List<QuestionDTO> questions = new ArrayList<>();
                    for(QuestionsExam qe: questionsExams){
                        QuestionDTO questionDTO = new QuestionDTO();
                        questionDTO.setIdQuestion(qe.getQuestion().getIdQuestion());
                        questionDTO.setQuestionText(qe.getQuestion().getQuestionText());
                        questionDTO.setIdExam(qe.getExam().getIdExam());


                        //gasesc raspunsurile corecte
                        CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(qe.getIdQuestionsExam()).orElse(null);
                        // transform raspunsurile corecte intr-o lista de stringuri
                        List<CorrectAnswersExamDTO> list = new ArrayList<>();
                        if(correctAnswersExam != null){
                            list.add(CorrectAnswersExamMapper.toDTO(correctAnswersExam));
                        }
                        questionDTO.setCorrectAnswers(list);

                        questions.add(questionDTO);
                    }
                    examDTO.setQuestion(questions);

                    //gasesc profesorii pentru examen
                    List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
                    List<UUID> teacherIds = new ArrayList<>();
                    for(TeacherExam te: teacherExams){
                        teacherIds.add(te.getTeacher().getIdUsers());
                    }
                    examDTO.setIdTeachers(teacherIds);

                    //gasesc studentii pentru examen

                    List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(exam.getIdExam());
                    List<StudentExamDTO> studentExamDTOs = studentExams.stream()
                            .map(StudentExamMapper::toDTO)
                            .collect(Collectors.toList());
                    examDTO.setStudentExamDTO(studentExamDTOs);


                    return examDTO;
                }

        ).collect(Collectors.toList());

    }

    @Override
    public void deleteExam(UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //daca sterg examenul, sterg si studentii care au dat examenul sau dau examenul
        List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(exam.getIdExam());
        studentExamRepository.deleteAll(studentExams);


        //am id question exam
        List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
        //sterg raspunsurile corecte
        for(QuestionsExam qe: questionsExams){
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(qe.getIdQuestionsExam()).orElse(null);
            if(correctAnswersExam != null){
                correctAnswersExamRepository.delete(correctAnswersExam);
            }
        }
        //acum sterg si id question exam
        questionsExamRepository.deleteAll(questionsExams);

        List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
        teacherExamRepository.deleteAll(teacherExams);

        //sterg intrebarile
        List<Question> questions = questionRepository.findAllByIdExam(exam.getIdExam());
        questionRepository.deleteAll(questions);

        examRepository.delete(exam);
    }

    @Override
    public void updateExam(ExamCreationDTO examCreationDTO, UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));


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

        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        Courses courseExam = exam.getCourse();
        //daca gasesc didactic cu profesorul si cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), courseExam.getIdCourses()).orElse(null);

        //acum am proful, cursul si curs-prof din didactic
        if(didactic == null){
            throw new NonAllowedException("You are not allowed to update this exam");
        }else{
            System.out.println("You are allowed to update this exam");

        }

//        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        if(examCreationDTO.getName() != null)
            exam.setName(examCreationDTO.getName());
        if(examCreationDTO.getTotalScore() != null)
            exam.setTotalScore(examCreationDTO.getTotalScore());
        if(examCreationDTO.getPassingScore() != null)
            exam.setPassingScore(examCreationDTO.getPassingScore());
        if(examCreationDTO.getDate() != null)
            exam.setDate(examCreationDTO.getDate());
        if(examCreationDTO.getTimeInMinutes() != null)
            exam.setTimeInMinutes(examCreationDTO.getTimeInMinutes());

        List<Question> questions = questionRepository.findAllByIdExam(exam.getIdExam());

            for (Question q : questions) {
                //daca gasesc intrebarea in lista de intrebari din examen
                //o actualizez
                List<QuestionCreationDTO> questionsDTO = examCreationDTO.getQuestion();
                if(questionsDTO != null) {
                    for (QuestionCreationDTO questionDTO : examCreationDTO.getQuestion()) {
                        if (q.getIdQuestion().equals(questionDTO.getIdQuestion())) {
                            if (questionDTO.getQuestionText() != null)
                                q.setQuestionText(questionDTO.getQuestionText());
                        }
                    }
                }
                questionRepository.save(q);

//                List<QuestionCreationDTO> questionsDTO = examCreationDTO.getQuestion();
//                if(questionsDTO != null) {
//                    for (QuestionCreationDTO questionDTO : examCreationDTO.getQuestion()) {
//
//                        if (q.getIdQuestion().equals(questionDTO.getIdQuestion())) {
//
//                            if (questionDTO.getQuestionText() != null)
//                                q.setQuestionText(questionDTO.getQuestionText());
//
//                        }
//
//                    }
//                }
//                questionRepository.save(q);
            }


        examRepository.save(exam);

    }

    @Override
    public List<ExamDTO> getExamsByCourse(UUID idCourse) {
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

        //am cursul pentru care vreau sa vad examenele
        Courses courses = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        //daca gasesc didactic cu profesorul si cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), courses.getIdCourses()).orElse(null);

        if (didactic == null) {
            throw new NonAllowedException("You are not allowed to see this course");
        } else {
            System.out.println("You are allowed to see this course");
        }


        List<Exam> exams = examRepository.findAllByCourses(idCourse).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        List<ExamDTO> examDTOS = new ArrayList<>();

        for(Exam exam: exams){
            ExamDTO examDTO = ExamMapper.toDTO(exam);

            //gasesc intrebarile pentru examen
            List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
            List<QuestionDTO> questions = new ArrayList<>();
            for(QuestionsExam qe: questionsExams){
                QuestionDTO questionDTO = new QuestionDTO();
                questionDTO.setIdQuestion(qe.getQuestion().getIdQuestion());
                questionDTO.setQuestionText(qe.getQuestion().getQuestionText());
                questionDTO.setIdExam(qe.getExam().getIdExam());


                //gasesc raspunsurile corecte
                CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(qe.getIdQuestionsExam()).orElse(null);
                // transform raspunsurile corecte intr-o lista de stringuri
                List<CorrectAnswersExamDTO> list = new ArrayList<>();
                if(correctAnswersExam != null){
                    list.add(CorrectAnswersExamMapper.toDTO(correctAnswersExam));
                }
                questionDTO.setCorrectAnswers(list);

                questions.add(questionDTO);
            }
            examDTO.setQuestion(questions);

            //gasesc profesorii pentru examen
            List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
            List<UUID> teacherIds = new ArrayList<>();
            for(TeacherExam te: teacherExams){
                teacherIds.add(te.getTeacher().getIdUsers());
            }
            examDTO.setIdTeachers(teacherIds);

            //gasesc studentii pentru examen
            List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(exam.getIdExam());
            List<StudentExamDTO> studentExamDTOs = studentExams.stream()
                    .map(StudentExamMapper::toDTO)
                    .collect(Collectors.toList());
            examDTO.setStudentExamDTO(studentExamDTOs);

            examDTOS.add(examDTO);
        }

        return examDTOS;
    }

    @Override
    public ExamDTO getExamById(UUID idExam) {
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

        Courses courseExam = exam.getCourse();

        //daca gasesc didactic cu profesorul si cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), courseExam.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to see this exam");
        }else{
            System.out.println("You are allowed to see this exam");
        }

        ExamDTO examDTO = ExamMapper.toDTO(exam);

        //gasesc intrebarile pentru examen
        List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
        List<QuestionDTO> questions = new ArrayList<>();
        for(QuestionsExam qe: questionsExams){
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setIdQuestion(qe.getQuestion().getIdQuestion());
            questionDTO.setQuestionText(qe.getQuestion().getQuestionText());
            questionDTO.setIdExam(qe.getExam().getIdExam());


            //gasesc raspunsurile corecte
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(qe.getIdQuestionsExam()).orElse(null);
            // transform raspunsurile corecte intr-o lista de stringuri
            List<CorrectAnswersExamDTO> list = new ArrayList<>();
            if(correctAnswersExam != null){
                list.add(CorrectAnswersExamMapper.toDTO(correctAnswersExam));
            }
            questionDTO.setCorrectAnswers(list);

            questions.add(questionDTO);
        }
        examDTO.setQuestion(questions);

        //gasesc profesorii pentru examen
        List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
        List<UUID> teacherIds = new ArrayList<>();
        for(TeacherExam te: teacherExams){
            teacherIds.add(te.getTeacher().getIdUsers());
        }
        examDTO.setIdTeachers(teacherIds);

        //gasesc studentii pentru examen
        List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(exam.getIdExam());
        List<StudentExamDTO> studentExamDTOs = studentExams.stream()
                .map(StudentExamMapper::toDTO)
                .collect(Collectors.toList());
        examDTO.setStudentExamDTO(studentExamDTOs);

        return examDTO;
    }

    @Override
    public List<StudentExamFrontDTO> getStudentsByExam(UUID idExam) {
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
        //iau linia din didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to see this exam");
        }else{
            System.out.println("You are allowed to see this exam");
        }

        List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(exam.getIdExam());
        List<StudentExamFrontDTO> studentExamFrontDTOS = new ArrayList<>();

        for(StudentExam se: studentExams){
            StudentExamFrontDTO studentExamFrontDTO = new StudentExamFrontDTO();
            studentExamFrontDTO.setIdStudentExam(se.getIdStudentExam());
            studentExamFrontDTO.setIdStudent(se.getStudent().getIdUsers());

            Students student = se.getStudent();
            Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new StudentExamNotFoundException("Student not found"));
            String studentName = user.getFirstName() + " " + user.getLastName();

            studentExamFrontDTO.setStudentName(studentName);
            studentExamFrontDTO.setIdExam(se.getExam().getIdExam());
            studentExamFrontDTO.setScore(se.getScore());
            studentExamFrontDTO.setExamStatus(se.getExamStatus());
            studentExamFrontDTOS.add(studentExamFrontDTO);
        }

        return studentExamFrontDTOS;
    }

    @Override
    public List<QuestionDTO> getQuestionsAndAnswersByExam(UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        List<QuestionDTO> questions = new ArrayList<>();

        List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());

        for(QuestionsExam qe: questionsExams){
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setIdQuestion(qe.getQuestion().getIdQuestion());
            questionDTO.setQuestionText(qe.getQuestion().getQuestionText());
            questionDTO.setIdExam(qe.getExam().getIdExam());

            //gasesc raspunsurile corecte
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(qe.getIdQuestionsExam()).orElse(null);
            // transform raspunsurile corecte intr-o lista de stringuri
            List<CorrectAnswersExamDTO> list = new ArrayList<>();
            if(correctAnswersExam != null){
                list.add(CorrectAnswersExamMapper.toDTO(correctAnswersExam));
            }
            questionDTO.setCorrectAnswers(list);

            questions.add(questionDTO);
        }

        return questions;
    }
}
