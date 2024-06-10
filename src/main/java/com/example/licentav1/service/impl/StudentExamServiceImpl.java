package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import com.example.licentav1.dto.StudentExamDetailsDTO;
import com.example.licentav1.dto.StudentExamFrontDTO;
import com.example.licentav1.mapper.StudentExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.StudentExamService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentsRepository studentsRepository;
    private final ExamRepository examRepository;
    private final StudentAnswersExamRepository studentAnswersExamRepository;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private final UsersRepository usersRepository;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final TeachersRepository teachersRepository;
    private final DidacticRepository didacticRepository;

    public StudentExamServiceImpl(StudentExamRepository studentExamRepository, StudentsRepository studentsRepository, ExamRepository examRepository, StudentAnswersExamRepository studentAnswersExamRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository, UsersRepository usersRepository, HttpServletRequest request, JwtService jwtService, TeachersRepository teachersRepository, DidacticRepository didacticRepository) {
        this.studentExamRepository = studentExamRepository;
        this.studentsRepository = studentsRepository;
        this.examRepository = examRepository;
        this.studentAnswersExamRepository = studentAnswersExamRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.usersRepository = usersRepository;
        this.request = request;
        this.jwtService = jwtService;
        this.teachersRepository = teachersRepository;
        this.didacticRepository = didacticRepository;
    }

    @Override
    public void createStudentExam(StudentExamCreationDTO studentExamCreationDTO) {
        //retireve the student and the exam from the database
        Students student = studentsRepository.findById(studentExamCreationDTO.getIdStudent()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Exam exam = examRepository.findById(studentExamCreationDTO.getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        // check if the student is enrolled in the course
        StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(student.getIdUsers(), exam.getCourse().getIdCourses()).orElseThrow(() -> new StudentNotFoundException("Student not enrolled in the course"));


        StudentExam studentExam = StudentExamMapper.fromDTO(studentExamCreationDTO, student, exam);

        studentExamRepository.save(studentExam);

    }

    @Override
    public List<StudentExamDTO> getAllStudentByExam(UUID idExam) {
        List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(idExam);
        List<StudentExamDTO> studentExamDTOS = new ArrayList<>();
        for (StudentExam studentExam : studentExams) {
            studentExamDTOS.add(StudentExamMapper.toDTO(studentExam));
        }

        return studentExamDTOS;
    }

    @Override
    public void uploadStudents(MultipartFile file, UUID idExam) throws IOException {
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


        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        //find the exam by id
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        //am examenul, iau cursul
        Courses course = exam.getCourse();
        //daca gasesc didactic cu profesorul si cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            StudentExam studentsExam = new StudentExam();
            //studentsExamCreationDTO.setIdStudent(UUID.fromString(data[0]));
            //find the student
            Students student = studentsRepository.findByNrMatriculation(data[0]).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            //set the student to the student-exam
            studentsExam.setStudent(student);
            //set the exam to the student-exam
            studentsExam.setExam(exam);
            //set the score to the student-exam
            studentsExam.setScore(-1);
            //set the exam status
            studentsExam.setExamStatus(-1);
            //save the student-exam to the database
            studentExamRepository.save(studentsExam);

        }
    }

    @Override
    public void deleteStudent(UUID idStudentExam) {
        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The row student-exam not found"));

        // for each row that has the id of the student exam, delete the row
        studentAnswersExamRepository.deleteAll(studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam()));

        //delete the student-exam row from the database
        studentExamRepository.delete(studentExam);
    }

    @Override
    public void updateStudentExam(UUID idStudentExam,StudentExamDTO studentExamDTO) {
        // Find the student exam entry
        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The student or the exam not found"));

        //update the fields
        if (studentExamDTO.getIdExam() != null){
            Exam exam = examRepository.findById(studentExamDTO.getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));
            studentExam.setExam(exam);
        }
        if (studentExamDTO.getIdStudent() != null){
            Students student = studentsRepository.findById(studentExamDTO.getIdStudent()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            studentExam.setStudent(student);
        }
        if (studentExamDTO.getScore() != null){
            studentExam.setScore(studentExamDTO.getScore());
        }
        if (studentExamDTO.getExamStatus() != null){
            studentExam.setExamStatus(studentExamDTO.getExamStatus());
        }

        //save the updated entry to the database
        studentExamRepository.save(studentExam);

    }

    @Override
    public StudentExamFrontDTO getStudentExamById(UUID idStudentExam) {
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

        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The student or the exam not found"));

        //gasesc examenul
        Exam exam = examRepository.findById(studentExam.getExam().getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));
        //gasesc cursul
        Courses course = exam.getCourse();

        //daca gasesc didactic cu profesorul si cursul respectiv
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("Teacher does not teach this course");
        }else{
            System.out.println("Teacher teaches this course");
        }

        Students student = studentsRepository.findById(studentExam.getStudent().getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Users users = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));

        return StudentExamFrontDTO.builder()
                .idStudentExam(studentExam.getIdStudentExam())
                .idStudent(studentExam.getStudent().getIdUsers())
                .studentName(users.getFirstName() + " " + users.getLastName())
                .idExam(studentExam.getExam().getIdExam())
                .score(studentExam.getScore())
                .examStatus(studentExam.getExamStatus())
                .build();
    }

    @Override
    public List<StudentExamDetailsDTO> getStudentExamByIdStudent() {
        //iau id ul din cookie
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
        Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));


        //find the exams that the student has by the courses that he is enrolled in
        List<StudentsFollowCourses> studentsFollowCourses = studentsFollowCoursesRepository.findAllCoursesByStudent(studentFromJwt.getIdUsers());

        if(studentsFollowCourses == null) {
            throw new StudentNotFoundException("Student not enrolled in any course");
        }

        //am cursuri, iau examenele de la cursuri
        List<Exam> exams = new ArrayList<>();
        for(StudentsFollowCourses studentsFollowCourse : studentsFollowCourses) {
            exams.addAll(examRepository.findAllByCourse(studentsFollowCourse.getCourse().getIdCourses()));
        }

        //am examenele, iau student-exam-urile
        List<StudentExamDetailsDTO> studentExamDetailsDTOS = new ArrayList<>();

        for(Exam exam : exams){
            List<StudentExam> studentExams = studentExamRepository.findAllByIdExam(exam.getIdExam());
            for(StudentExam studentExam : studentExams){
                if(studentExam.getStudent().getIdUsers().equals(studentFromJwt.getIdUsers())){
                    Users users = usersRepository.findById(studentExam.getStudent().getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
                    studentExamDetailsDTOS.add(StudentExamDetailsDTO.builder()
                            .idStudentExam(studentExam.getIdStudentExam())
                            .idStudent(studentExam.getStudent().getIdUsers())
                            .courseName(exam.getCourse().getName())
                            .idExam(studentExam.getExam().getIdExam())
                            .score(studentExam.getScore())
                            .examStatus(studentExam.getExamStatus())
                            .build());
                }
            }
        }


        return studentExamDetailsDTOS;

    }


}
