package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;
import com.example.licentav1.email.EmailDetails;
import com.example.licentav1.email.EmailService;
import com.example.licentav1.mapper.HomeworkAnnouncementsMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.HomeworkAnnouncementsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HomeworkAnnouncementsServiceImpl implements HomeworkAnnouncementsService {
    private final HomeworkAnnouncementsRepository homeworkAnnouncementsRepository;
    private final LecturesRepository lectureRepository;
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final TeachersRepository teachersRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final StudentHomeworkRepository studentHomeworkRepository;

    public HomeworkAnnouncementsServiceImpl(HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, LecturesRepository lectureRepository, CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teachersRepository, JwtService jwtService, HttpServletRequest request, StudentsFollowCoursesRepository studentsFollowCoursesRepository, StudentsRepository studentsRepository, UsersRepository usersRepository, EmailService emailService, StudentHomeworkRepository studentHomeworkRepository) {
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.lectureRepository = lectureRepository;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teachersRepository = teachersRepository;
        this.jwtService = jwtService;
        this.request = request;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
        this.emailService = emailService;
        this.studentHomeworkRepository = studentHomeworkRepository;
    }

    @Override
    public void createHomeworkAnnouncement(UUID idLecture, HomeworkAnnouncementsCreationDTO homeworkAnnouncementsCreationDTO) {
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

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
//        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //get the lecture
        Lectures lecture = lectureRepository.findById(idLecture).orElseThrow(() -> new LectureNotFoundException("Lecture not found!"));

        //get the course
        Courses course = coursesRepository.findById(lecture.getCourses().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found!"));


        //get the authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //get the user id from JWT
        UUID userId = jwtService.extractUserId((Users) authentication.getPrincipal());

//        Didactic didactic = didacticRepository.findByIdCourses(course.getIdCourses()).orElseThrow(() -> new DidacticRelationNotFoundException("Didactic relation not found!"));

        Teachers teacher = teachersRepository.findById(userId).orElseThrow(() -> new DidacticRelationNotFoundException("Teacher not found!"));

        //verify if the teacher is one of the owner of the course
        if (!teacher.getCourses().contains(course)) {
            throw new NonAllowedException("Teacher is not the owner of the course!");
        }
//        else {
//            System.out.println("Teacher is the owner of the course!");
//        }

        //create the homework announcement
        HomeworkAnnouncements homeworkAnnouncements = HomeworkAnnouncementsMapper.fromDTO(homeworkAnnouncementsCreationDTO, lecture);

        //save the homework announcement
        homeworkAnnouncementsRepository.save(homeworkAnnouncements);
    }

    @Override
    public List<HomeworkAnnouncementsDTO> getHomeworkAnnouncements(UUID idLecture) {
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

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        String role = jwtService.extractRole(token);
//        System.out.println("id from token: " + id);
//        System.out.println("AICI: " + role);

        //get the lecture
        Lectures lecture = lectureRepository.findById(idLecture).orElseThrow(() -> new LectureNotFoundException("Lecture not found!"));

        //am lecture, iau course
        Courses course = lecture.getCourses();

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));


            //am course, iau didactic
            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(didactic == null) {
                throw new NonAllowedException("You are not allowed to see this homework announcement!");
            }
//            else {
//                System.out.println("You are allowed to see this homework announcement!");
//            }
        }else if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to see this homework announcement!");
            }
//            else{
//                System.out.println("You are allowed to see this homework announcement!");
//            }
        }

        //get all the homework announcements for the lecture
        List<HomeworkAnnouncements> homeworkAnnouncements = homeworkAnnouncementsRepository.findAllByLectures(lecture);

        List<HomeworkAnnouncementsDTO> homeworkAnnouncementsDTO = new ArrayList<>();

        for (HomeworkAnnouncements homeworkAnnouncement : homeworkAnnouncements) {
            homeworkAnnouncementsDTO.add(HomeworkAnnouncementsMapper.toDTO(homeworkAnnouncement));
        }

        return homeworkAnnouncementsDTO;
    }

    @Override
    public void deleteHomeworkAnnouncement(UUID idHomeworkAnnouncement) {
        homeworkAnnouncementsRepository.deleteById(idHomeworkAnnouncement);
    }

    @Override
    public void updateHomeworkAnnouncement(UUID idHomeworkAnnouncement, HomeworkAnnouncementsDTO homeworkAnnouncementsDTO) {
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

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
//        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //get the homework announcement
        HomeworkAnnouncements homeworkAnnouncement = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new LectureNotFoundException("Homework announcement not found!"));

        //am hw announcement, iau lecture
        Lectures lecture = homeworkAnnouncement.getLectures();

        //am lecture, iau course
        Courses course = lecture.getCourses();

        //am course si teacher, iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if (didactic == null) {
            throw new NonAllowedException("You are not allowed to update this homework announcement!");
        }
//        else {
//            System.out.println("You are allowed to update this homework announcement!");
//        }

        //update the homework announcement
        if (homeworkAnnouncementsDTO.getTitle() != null) {
            homeworkAnnouncement.setTitle(homeworkAnnouncementsDTO.getTitle());
        }
        if (homeworkAnnouncementsDTO.getDescription() != null) {
            homeworkAnnouncement.setDescription(homeworkAnnouncementsDTO.getDescription());
        }
        if (homeworkAnnouncementsDTO.getDueDate() != null) {
            System.out.println(homeworkAnnouncementsDTO.getDueDate());
            homeworkAnnouncement.setDueDate(homeworkAnnouncementsDTO.getDueDate());
        }
        if (homeworkAnnouncementsDTO.getScore() != null) {
            homeworkAnnouncement.setScore(homeworkAnnouncementsDTO.getScore());
        }


        //save the homework announcement
        homeworkAnnouncementsRepository.save(homeworkAnnouncement);
    }


    @Override
    public HomeworkAnnouncementsDTO getHomeworkAnnouncement(UUID idHomeworkAnnouncement) {
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

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        String role = jwtService.extractRole(token);
//        System.out.println("id from token: " + id);

        HomeworkAnnouncements hA = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new LectureNotFoundException("Homework announcement not found!"));

        //am hw announcement, iau lecture
        Lectures lecture = hA.getLectures();

        //am lecture, iau course
        Courses course = lecture.getCourses();

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

            //am course si teacher, iau didactic
            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if (didactic == null) {
                throw new NonAllowedException("You are not allowed to see this homework announcement!");
            }
//            else {
//                System.out.println("You are allowed to see this homework announcement!");
//            }
        }else if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to see this homework announcement!");
            }else{
                System.out.println("You are allowed to see this homework announcement!");
            }
        }






        return HomeworkAnnouncementsMapper.toDTO(hA);
    }


    //    @Scheduled(cron = "0 0 0 * * ?") // This will run the method every day at midnight
    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Bucharest")
    public void sendHomeworkReminderEmails() {
        try {
            System.out.println("Sending homework reminder emails");

            LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

            // Get all homework announcements where the DueDate is tomorrow
            List<HomeworkAnnouncements> homeworkDueTomorrow = homeworkAnnouncementsRepository.findAllByDueDate(tomorrow);

            for (HomeworkAnnouncements homework : homeworkDueTomorrow) {
                System.out.println("Homework due tomorrow: " + homework.getTitle());
                // Get all students who are supposed to submit this homework
                List<StudentsFollowCourses> studentsFollowCourses = studentsFollowCoursesRepository.findAllByCourse(homework.getLectures().getCourses().getIdCourses()).orElseThrow(() -> new StudentCourseRelationNotFoundException("Student-Course relation not found"));

                List<Students> students = new ArrayList<>();
                for (StudentsFollowCourses studentFollowCourse : studentsFollowCourses) {
                    System.out.println("Student: " + studentFollowCourse.getStudent().getIdUsers());
                    students.add(studentsRepository.findById(studentFollowCourse.getStudent().getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found")));
                }

                for (Students student : students) {
                    Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new UserNotFoundException("User not found"));

                    //verific daca acest user a trimis deja homework-ul
                    StudentHomework studentHomework = studentHomeworkRepository.findByIdStudentAndIdHomeworkAnnouncement(student.getIdUsers(), homework.getIdHomeworkAnnouncements()).orElse(null);
                    System.out.println("Student homework: " + studentHomework);
                    if(studentHomework != null){
                        System.out.println("Student already submitted homework");
                    }else{
                        System.out.println("Sending email to: " + user.getFacultyEmail());
                        System.out.println("Homework title: " + homework.getTitle());
                        System.out.println("Course name: " + homework.getLectures().getCourses().getName());
                        // Send the email
                        emailService.sendReminderHomeworkStyle(user.getFacultyEmail(), homework.getTitle(), homework.getLectures().getCourses().getName());
                    }

                }
            }
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }
}
