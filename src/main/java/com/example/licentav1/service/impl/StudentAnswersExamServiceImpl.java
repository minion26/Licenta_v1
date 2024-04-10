package com.example.licentav1.service.impl;

import com.example.licentav1.domain.*;
import com.example.licentav1.dto.QuestionAnswersDTO;
import com.example.licentav1.dto.StudentAnswersExamCreationDTO;
import com.example.licentav1.mapper.ReviewStudentAnswersMapper;
import com.example.licentav1.mapper.StudentAnswersExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.ExamService;
import com.example.licentav1.service.StudentAnswersExamService;
import org.springframework.stereotype.Service;
import com.example.licentav1.dto.ReviewStudentAnswersDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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


    public StudentAnswersExamServiceImpl(StudentAnswersExamRepository studentAnswersExamRepository, StudentExamRepository studentExamRepository, QuestionsExamRepository questionsExamRepository, ExamRepository examRepository, StudentsRepository studentsRepository, CorrectAnswersExamRepository correctAnswersExamRepository, CoursesRepository coursesRepository, DidacticRepository didacticRepository) {
        this.studentAnswersExamRepository = studentAnswersExamRepository;
        this.studentExamRepository = studentExamRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.examRepository = examRepository;
        this.studentsRepository = studentsRepository;
        this.correctAnswersExamRepository = correctAnswersExamRepository;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
    }


    @Override
    public void submitExamAnswers(StudentAnswersExamCreationDTO studentAnswersExamCreationDTO) {
        // iau id ul studentului si id ul examenului din fieldul idStudentExam
        StudentExam studentExam = studentExamRepository.findById(studentAnswersExamCreationDTO.getIdStudentExam()).orElseThrow(() -> new RuntimeException("Student exam not found"));

        UUID idExam = studentExam.getExam().getIdExam();

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
            // trim the correctAnswer of any leading or trailing whitespaces
            correctAnswer = correctAnswer.trim();

            // compare the student answer with the correct answer
            int differences = compareAnswers(studentAnswer, correctAnswer);
            System.out.println("Differences: " + differences);
            if (differences <= 2) {
                score += correctAnswersExam.getScore();
                System.out.println("E OK! Score: " + score);
            }else{
                // coloana needs_review din tabelul student_answers_exam va fi setata pe true
                // si o sa apara in lista de raspunsuri care trebuie revizuite
                studentAnswersExam.setNeedsReview(true);
                studentAnswersExamRepository.save(studentAnswersExam);
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
        List<ReviewStudentAnswersDTO> reviewStudentAnswersDTOS = new ArrayList<>();
        //for each exam
        List<Exam> exams = examRepository.findAll();
        for (Exam exam : exams){
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
            for (StudentExam studentExam : studentExams){
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


        return reviewStudentAnswersDTOS;

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
