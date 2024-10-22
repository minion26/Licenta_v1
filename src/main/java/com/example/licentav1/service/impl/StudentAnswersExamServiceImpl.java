package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.AnswerNotFoundException;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.*;
import com.example.licentav1.mapper.ReviewStudentAnswersMapper;
import com.example.licentav1.mapper.StudentAnswersExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.ExamService;
import com.example.licentav1.service.StudentAnswersExamService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentAnswersExamServiceImpl implements StudentAnswersExamService {
    private final StudentAnswersExamRepository studentAnswersExamRepository;
    private final StudentExamRepository studentExamRepository;
    private final QuestionsExamRepository questionsExamRepository;
    private final ExamRepository examRepository;
    private final StudentsRepository studentsRepository;
    private final CorrectAnswersExamRepository correctAnswersExamRepository;
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final TeachersRepository teacherRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;


    public StudentAnswersExamServiceImpl(StudentAnswersExamRepository studentAnswersExamRepository, StudentExamRepository studentExamRepository, QuestionsExamRepository questionsExamRepository, ExamRepository examRepository, StudentsRepository studentsRepository, CorrectAnswersExamRepository correctAnswersExamRepository, CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teacherRepository, JwtService jwtService, HttpServletRequest request) {
        this.studentAnswersExamRepository = studentAnswersExamRepository;
        this.studentExamRepository = studentExamRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.examRepository = examRepository;
        this.studentsRepository = studentsRepository;
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teacherRepository = teacherRepository;
        this.jwtService = jwtService;
        this.request = request;
    }


    @Override
    public void submitExamAnswers(StudentAnswersExamCreationDTO studentAnswersExamCreationDTO) {
        // iau id ul studentului si id ul examenului din fieldul idStudentExam
        StudentExam studentExam = studentExamRepository.findById(studentAnswersExamCreationDTO.getIdStudentExam()).orElseThrow(() -> new RuntimeException("Student exam not found"));

        UUID idExam = studentExam.getExam().getIdExam();

        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.getStartTime() != null &&
                Duration.between(exam.getStartTime(), LocalDateTime.now()).toMinutes() > exam.getTimeInMinutes()) {
            // examenul a expirat, nu mai sunt acceptate raspunsuri
            //sa pun ca studentul are 0 puncte si statusul examenului este 0
            studentExam.setScore(0);
            studentExam.setExamStatus(0);
            studentExamRepository.save(studentExam);
            throw new NonAllowedException("The exam has ended. Answers are no longer accepted.");
        }

        UUID idStudent = studentExam.getStudent().getIdUsers();

        int score = 0;

        for (QuestionAnswersDTO questionAnswersDTO : studentAnswersExamCreationDTO.getAnswers()) {
            // testez daca id ul examenului este acelasi cu id ul examenului din fieldul idQuestionExam
            QuestionsExam questionsExam = questionsExamRepository.findById(questionAnswersDTO.getIdQuestionExam()).orElseThrow(() -> new RuntimeException("Question exam not found"));

            UUID idQuestionExam = questionsExam.getExam().getIdExam();

            // iau obiectul de tip CorrectAnswersExam care are id ul egal cu id ul intrebarii din fieldul idQuestionExam
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElseThrow(() -> new RuntimeException("Correct answers exam not found"));
//            System.out.println("Correct answers exam: " + correctAnswersExam.getCorrectAnswer());

            // testez daca id ul examenului este acelasi cu id ul examenului din fieldul idQuestionExam
            if (idExam != questionsExam.getExam().getIdExam()) {
                throw new RuntimeException("Question exam not found");
            }

            Question question = questionsExam.getQuestion();

            // creez un obiect de tip StudentAnswersExam
            StudentAnswersExam studentAnswersExam = StudentAnswersExamMapper.fromDTO(questionAnswersDTO, studentExam, questionsExam);


            // salvez obiectul in baza de date
            studentAnswersExamRepository.save(studentAnswersExam);

            // Compare student's answer with correct answer and increment score if correct
            String studentAnswer = studentAnswersExam.getStudentAnswer();
            String correctAnswer = correctAnswersExam.getCorrectAnswer();

            // trim the studentAnswer of any leading or trailing whitespaces
            studentAnswer = studentAnswer.trim();
            System.out.println("Student answer: " + studentAnswer);
            // trim the correctAnswer of any leading or trailing whitespaces
            correctAnswer = correctAnswer.trim();
            System.out.println("Correct answer: " + correctAnswer);

            // compare the student answer with the correct answer
            if(studentAnswer.equals(correctAnswer)) {
                score += correctAnswersExam.getScore();
                System.out.println("OK! Score: " + score);
            }else {
                int differences = compareAnswers(studentAnswer, correctAnswer);
                System.out.println("Differences: " + differences);
                if (differences <= 2) {
                    // studentul poate avea un typo si ii trimit profesorului notificare
                    // si o sa apara in lista de raspunsuri care trebuie revizuite
                    studentAnswersExam.setNeedsReview(true);
                    studentAnswersExamRepository.save(studentAnswersExam);
//                    score += correctAnswersExam.getScore();
//                    System.out.println("E OK! Score: " + score);
                } else {
                    //inseamna ca diferenta este prea mare si studentul nu primeste punctaj
                        score += 0;
//                    studentAnswersExam.setNeedsReview(true);
//                    studentAnswersExamRepository.save(studentAnswersExam);
                }
            }

            // update the score of the student exam
            studentExam.setScore(score);

            // save the student exam
            studentExamRepository.save(studentExam);

            // update the exam status of the student exam
            Integer passingScore = studentExam.getExam().getPassingScore();
            if (score >= passingScore){
                studentExam.setExamStatus(1);
            } else {
                studentExam.setExamStatus(0);
            }

            // save the student exam
            studentExamRepository.save(studentExam);


        }

    }


    @Override
    public void deleteStudentAnswers(UUID idExam, UUID idStudent) {
        // find the student exam by idExam and idStudent
        StudentExam studentExam = studentExamRepository.findByIdStudentAndIdExam(idStudent, idExam).orElseThrow(() -> new RuntimeException("Student exam not found"));

        System.out.println("Student exam: " + studentExam.getIdStudentExam());

        //reset the score of the student exam and the exam status
        studentExam.setScore(-1);
        studentExam.setExamStatus(-1);


        // for each row that has the id of the student exam, delete the row
        studentAnswersExamRepository.deleteAll(studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam()));

    }

    @Override
    public List<StudentAnswersExamCreationDTO> getStudentAnswers(UUID idExam, UUID idStudent) {
        // get the student exam by idExam and idStudent
        StudentExam studentExam = studentExamRepository.findByIdStudentAndIdExam(idStudent, idExam).orElseThrow(() -> new RuntimeException("Student exam not found"));

        // get the list of student answers exams by student exam
        List<StudentAnswersExam> studentAnswersExams = studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam());

        // map the list of student answers exams to a list of student answers exam creation DTOs
        return StudentAnswersExamMapper.toDTOs(studentAnswersExams);
    }

    @Override
    public List<StudentAnswersExamCreationDTO> getAllStudentsAnswers(UUID idExam) {
        // get a list of all students that took the exam
        List<StudentExam> studentExams = studentExamRepository.findAllByIdExam(idExam);

        // list to hold all the student answers exams
        List<StudentAnswersExamCreationDTO> allStudentAnswers = new ArrayList<>();

        // for each student, retrieve all their answers
        for (StudentExam studentExam : studentExams){
            // get the list of student answers exams by student exam
            List<StudentAnswersExam> studentAnswersExams = studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam());

            // map the list of student answers exams to a list of student answers exam creation DTOs
            allStudentAnswers.addAll(StudentAnswersExamMapper.toDTOs(studentAnswersExams));
        }

        return allStudentAnswers;
    }

    @Override
    public List<ReviewStudentAnswersDTO> getStudentsAnswersForReview() {
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
//        System.out.println("id from token: " + idToken);
        Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        // Get the list of didactics for the teacher
        List<Didactic> didactics = didacticRepository.findAllByIdTeacher(teacherFromJwt.getIdUsers()).orElse(Collections.emptyList());

        // Convert the list of didactics to a list of course IDs

        List<UUID> courseIds = didactics.stream().map(didactic -> didactic.getCourses().getIdCourses()).toList();

        List<ReviewStudentAnswersDTO> reviewStudentAnswersDTOS = new ArrayList<>();
        //for each exam
        List<Exam> exams = examRepository.findAll();
        for (Exam exam : exams){
//            System.out.println("Exam: " + exam.getCourse().getName());
            if (courseIds.contains(exam.getCourse().getIdCourses())) {
                UUID idExam = exam.getIdExam();

                // get the course of the exam
                Courses course = coursesRepository.findById(exam.getCourse().getIdCourses()).orElseThrow(() -> new RuntimeException("Course not found"));

                //get the didactic of the course
                List<Didactic> didactic = didacticRepository.findAllByIdCourses(course.getIdCourses()).orElseThrow(() -> new RuntimeException("Didactic not found"));

                //get the list of teachers
                List<Teachers> teachers = new ArrayList<>();
                for (Didactic did : didactic){
                    teachers.add(did.getTeachers());
                }

                // list of all students that took the exam
                List<StudentExam> studentExams = studentExamRepository.findAllByIdExam(idExam);

                // for each student, retrieve all their answers that need review
                for (StudentExam studentExam : studentExams) {
                    // get the list of student answers exams by student exam where needsReview is true
                    List<StudentAnswersExam> studentAnswersExams = studentAnswersExamRepository.findAllByStudentExamAndNeedsReview(studentExam.getIdStudentExam(), true);

                    // map the list of student answers exams to a list of review student answers DTOs
                    for (StudentAnswersExam studentAnswersExam : studentAnswersExams) {
                        ReviewStudentAnswersDTO reviewStudentAnswersDTO = ReviewStudentAnswersMapper.toDTO(studentExam, studentAnswersExam, exam, teachers);

                        // add the DTO to the list
                        reviewStudentAnswersDTOS.add(reviewStudentAnswersDTO);
                    }
                }
            }
        }


        return reviewStudentAnswersDTOS;

    }
//trebuie sa o fac endpoint ca sa o pot apela din front
    @Override
    public void setReviewed(UUID idStudentAnswerExam, CorrectAnswersExamCreationDTO correctAnswersExamCreationDTO) {
        // get the student answer exam by id
        StudentAnswersExam studentAnswersExam = studentAnswersExamRepository.findById(idStudentAnswerExam).orElseThrow(() -> new AnswerNotFoundException("Student answer exam not found"));

        // get the student exam
        StudentExam studentExam = studentAnswersExam.getStudentExam();

        // set the needs review to false
        studentAnswersExam.setNeedsReview(false);

        int passingScore = studentExam.getExam().getPassingScore();

        if (correctAnswersExamCreationDTO.getScore() != null) {
            // change the score of the student exam
            int newScoreFromTeacher = correctAnswersExamCreationDTO.getScore();
            System.out.println("new score from teacher: " + newScoreFromTeacher);

            // get the correct answers exam
            CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(studentAnswersExam.getQuestionsExam().getIdQuestionsExam()).orElseThrow(() -> new RuntimeException("Correct answers exam not found"));

            System.out.println("correct answer: " + correctAnswersExam.getCorrectAnswer());

            // get the score of the correct answer
            int correctScore = correctAnswersExam.getScore();

            System.out.println("correct score: " + correctScore);

            // update the score of the student exam
            int putNewScore = studentExam.getScore(); //scad scorul vechi
            studentExam.setScore(studentExam.getScore() + newScoreFromTeacher);

            if(studentExam.getScore() < passingScore){
                studentExam.setExamStatus(0);
            } else {
                studentExam.setExamStatus(1);
            }

        }
        // save the student exam
        studentExamRepository.save(studentExam);
        System.out.println("new student exam score: " + studentExam.getScore());

        // save the student answer exam
        studentAnswersExamRepository.save(studentAnswersExam);
    }

    @Override
    public ReviewStudentAnswersDTO getStudentAnswerForReview(UUID idStudentAnswerExam) {
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
//        System.out.println("id from token: " + idToken);
        Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        // Get the list of didactics for the teacher
        List<Didactic> didactics = didacticRepository.findAllByIdTeacher(teacherFromJwt.getIdUsers()).orElse(Collections.emptyList());

        // Convert the list of didactics to a list of course IDs

        List<UUID> courseIds = didactics.stream().map(didactic -> didactic.getCourses().getIdCourses()).toList();

        // get the student answer exam by id
        StudentAnswersExam studentAnswersExam = studentAnswersExamRepository.findById(idStudentAnswerExam).orElseThrow(() -> new AnswerNotFoundException("Student answer exam not found"));

        // get the student exam
        StudentExam studentExam = studentAnswersExam.getStudentExam();

        // get the exam
        Exam exam = studentExam.getExam();

        // get the course of the exam
        Courses course = coursesRepository.findById(exam.getCourse().getIdCourses()).orElseThrow(() -> new RuntimeException("Course not found"));

        //get the didactic of the course
        List<Didactic> didactic = didacticRepository.findAllByIdCourses(course.getIdCourses()).orElseThrow(() -> new RuntimeException("Didactic not found"));

        //get the list of teachers
        List<Teachers> teachers = new ArrayList<>();
        for (Didactic did : didactic){
            teachers.add(did.getTeachers());
        }

        // check if the teacher teaches the course of the exam
        if (courseIds.contains(course.getIdCourses())) {
            return ReviewStudentAnswersMapper.toDTO(studentExam, studentAnswersExam, exam, teachers);
        } else {
            throw new NonAllowedException("Teacher does not teach the course of the exam");
        }



    }

    @Override
    public List<QuestionAndStudentsAnswersDTO> getStudentsAnswers(UUID idExam, UUID idStudent) {
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
//        System.out.println("id from token: " + idToken);
        Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new RuntimeException("Exam not found"));

        //iau cursul examenului
        Courses course = coursesRepository.findById(exam.getCourse().getIdCourses()).orElseThrow(() -> new RuntimeException("Course not found"));
        //iau didacticul cursului
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if (didactic == null) {
            throw new NonAllowedException("Teacher does not teach the course of the exam");
        }
//        else{
//            System.out.println("Teacher teaches the course of the exam");
//        }



        // get the student exam by idExam and idStudent
        StudentExam studentExam = studentExamRepository.findByIdStudentAndIdExam(idStudent, idExam).orElseThrow(() -> new RuntimeException("Student exam not found"));

        // get the list of student answers exams by student exam
        List<StudentAnswersExam> studentAnswersExams = studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam());

        // map the list of student answers exams to a list of student answers exam creation DTOs
        List<QuestionAndStudentsAnswersDTO> questionAndStudentsAnswersDTOS = new ArrayList<>();
        for (StudentAnswersExam studentAnswersExam : studentAnswersExams){
            QuestionAndStudentsAnswersDTO questionAndStudentsAnswersDTO = new QuestionAndStudentsAnswersDTO();
            QuestionsExam questionsExam = studentAnswersExam.getQuestionsExam();
            if (questionsExam != null) {
                Question question = questionsExam.getQuestion();
                if (question != null) {
                    questionAndStudentsAnswersDTO.setQuestionText(question.getQuestionText());
                } else {
                    System.out.println("Question is null for QuestionsExam with ID: " + questionsExam.getIdQuestionsExam());
                }
                //iau si raspuunsul corect
                CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElseThrow(() -> new RuntimeException("Correct answers exam not found"));
                questionAndStudentsAnswersDTO.setCorrectAnswer(correctAnswersExam.getCorrectAnswer());
                //iau si scorul
                questionAndStudentsAnswersDTO.setScore(correctAnswersExam.getScore());
            } else {
                System.out.println("QuestionsExam is null for StudentAnswersExam with ID: " + studentAnswersExam.getIdStudentAnswerExam());
            }
            questionAndStudentsAnswersDTO.setStudentAnswer(studentAnswersExam.getStudentAnswer());
            questionAndStudentsAnswersDTOS.add(questionAndStudentsAnswersDTO);
        }

        return questionAndStudentsAnswersDTOS;
    }

    @Override
    public List<QuestionAndStudentsAnswersDTO> getMyAnswers(UUID idExam) {
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
//        System.out.println("id from token: " + idToken);

        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new RuntimeException("Exam not found"));

        //iau cursul examenului
        Courses course = coursesRepository.findById(exam.getCourse().getIdCourses()).orElseThrow(() -> new RuntimeException("Course not found"));

        // get the student exam by idExam and idStudent
        StudentExam studentExam = studentExamRepository.findByIdStudentAndIdExam(idToken, idExam).orElseThrow(() -> new RuntimeException("Student exam not found"));

        // get the list of student answers exams by student exam
        List<StudentAnswersExam> studentAnswersExams = studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam());

        // map the list of student answers exams to a list of student answers exam creation DTOs
        List<QuestionAndStudentsAnswersDTO> questionAndStudentsAnswersDTOS = new ArrayList<>();
        for (StudentAnswersExam studentAnswersExam : studentAnswersExams){
            QuestionAndStudentsAnswersDTO questionAndStudentsAnswersDTO = new QuestionAndStudentsAnswersDTO();
            QuestionsExam questionsExam = studentAnswersExam.getQuestionsExam();
            if (questionsExam != null) {
                Question question = questionsExam.getQuestion();
                if (question != null) {
                    questionAndStudentsAnswersDTO.setQuestionText(question.getQuestionText());
                } else {
                    System.out.println("Question is null for QuestionsExam with ID: " + questionsExam.getIdQuestionsExam());
                }
                //iau si raspuunsul corect
                CorrectAnswersExam correctAnswersExam = correctAnswersExamRepository.findByIdQuestionExam(questionsExam.getIdQuestionsExam()).orElseThrow(() -> new RuntimeException("Correct answers exam not found"));
                questionAndStudentsAnswersDTO.setCorrectAnswer(correctAnswersExam.getCorrectAnswer());
                //iau si scorul
                questionAndStudentsAnswersDTO.setScore(correctAnswersExam.getScore());
            } else {
                System.out.println("QuestionsExam is null for StudentAnswersExam with ID: " + studentAnswersExam.getIdStudentAnswerExam());
            }
            questionAndStudentsAnswersDTO.setStudentAnswer(studentAnswersExam.getStudentAnswer());
            questionAndStudentsAnswersDTOS.add(questionAndStudentsAnswersDTO);
        }

        return questionAndStudentsAnswersDTOS;

    }


    private int compareAnswers(String studentAnswer, String correctAnswer) {
        int[][] dp = new int[studentAnswer.length() + 1][correctAnswer.length() + 1];

        for (int i = 0; i <= studentAnswer.length(); i++) {
            for (int j = 0; j <= correctAnswer.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1]
                                    + costOfSubstitution(studentAnswer.charAt(i - 1), correctAnswer.charAt(j - 1)), dp[i - 1][j] + 1),
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[studentAnswer.length()][correctAnswer.length()];
    }

    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
}
