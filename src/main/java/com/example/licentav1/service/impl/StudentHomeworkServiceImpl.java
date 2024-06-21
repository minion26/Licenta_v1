package com.example.licentav1.service.impl;

import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.StudentHomeworkDTO;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.StudentHomeworkService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentHomeworkServiceImpl implements StudentHomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final StudentHomeworkRepository studentHomeworkRepository;
    private final HomeworkFilesRepository homeworkFilesRepository;
    private final StudentsRepository studentsRepository;
    private final S3Service s3Service;
    private final HomeworkAnnouncementsRepository homeworkAnnouncementsRepository;
    private final UsersRepository usersRepository;
    private final HttpServletRequest request;
    private final TeachersRepository teachersRepository;
    private final JwtService jwtService;
    private final DidacticRepository didacticRepository;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;

    public StudentHomeworkServiceImpl(HomeworkRepository homeworkRepository, StudentHomeworkRepository studentHomeworkRepository, HomeworkFilesRepository homeworkFilesRepository, StudentsRepository studentsRepository, S3Service s3Service, HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, UsersRepository usersRepository, HttpServletRequest request, TeachersRepository teachersRepository, JwtService jwtService, DidacticRepository didacticRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository) {
        this.homeworkRepository = homeworkRepository;
        this.studentHomeworkRepository = studentHomeworkRepository;
        this.homeworkFilesRepository = homeworkFilesRepository;
        this.studentsRepository = studentsRepository;
        this.s3Service = s3Service;
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.usersRepository = usersRepository;
        this.request = request;
        this.teachersRepository = teachersRepository;
        this.jwtService = jwtService;
        this.didacticRepository = didacticRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
    }

    @Override
    public Boolean checkPost(UUID idHomeworkAnnouncement, UUID idStudent) {
        //vreau sa verific daca studentul a postat homework-ul
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
        System.out.println("id from token: " + id);

        HomeworkAnnouncements hA = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new RuntimeException("Homework announcement not found"));
        Lectures lecture = hA.getLectures();
        Courses courses = lecture.getCourses();

        if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), courses.getIdCourses()).orElse(null);

            if(studentsFollowCourses == null){
                throw new NonAllowedException("Student not enrolled in course");
            }else{
                System.out.println("Student enrolled in course");
            }
        }

        //iau din StudentHomework idHomework
        StudentHomework studentHomework = studentHomeworkRepository.findByIdStudentAndIdHomeworkAnnouncement(idStudent, idHomeworkAnnouncement).orElse(null);

        if(studentHomework == null){ //daca nu exista in StudentHomework
            throw new NonAllowedException("Student homework not found");

        }

        HomeworkFiles homeworkFiles = homeworkFilesRepository.findByIdHomework(studentHomework.getHomework().getIdHomework()).orElseThrow(() -> new StudentNotFoundException("Homework files not found"));

        //compar idHomework din StudentHomework cu idHomework din HomeworkFiles
        return homeworkFiles.getHomework().getIdHomework().equals(studentHomework.getHomework().getIdHomework());

    }


    @Override
    public List<StudentHomeworkDTO> getAllByStudent() {
        //vreau sa verific daca studentul a postat homework-ul
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
        System.out.println("id from token: " + id);

        Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));


        List<StudentHomeworkDTO> listStudentHomeworkDTO = new ArrayList<>();
        //iau din StudentHomework idHomework
        List<StudentHomework> studentHomework = studentHomeworkRepository.findByIdStudent(id).orElse(null);

        if(studentHomework == null){ //daca nu exista in StudentHomework
            throw new NonAllowedException("Student homework not found");

        }

        for (StudentHomework studentHomework1 : studentHomework) {
            List<HomeworkFiles> listFiles = homeworkFilesRepository.findAllByHomework(studentHomework1.getHomework());

            // Daca lista de fisiere este goala, treci la urmatorul studentHomework
            if (listFiles.isEmpty()) {
                continue;
            }

            StudentHomeworkDTO studentHomeworkDTO = new StudentHomeworkDTO();
            studentHomeworkDTO.setIdStudentHomework(studentHomework1.getIdStudentHomework());
            studentHomeworkDTO.setIdHomework(studentHomework1.getHomework().getIdHomework());
            studentHomeworkDTO.setIdStudent(studentFromJwt.getIdUsers());

            for (HomeworkFiles homeworkFiles : listFiles) {
                studentHomeworkDTO.getIdHomeworkFiles().add(homeworkFiles.getIdHomeworkFiles());
            }

            HomeworkAnnouncements homeworkAnnouncements = studentHomework1.getHomeworkAnnouncements();
            studentHomeworkDTO.setHomeworkName(homeworkAnnouncements.getTitle());

            Homework homework = studentHomework1.getHomework();
            studentHomeworkDTO.setGrade(homework.getGrade());

            listStudentHomeworkDTO.add(studentHomeworkDTO);
        }

        return listStudentHomeworkDTO;

//        studentHomeworkDTO.setIdStudentHomework(studentHomework.getIdStudentHomework());
//        studentHomeworkDTO.setIdHomework(studentHomework.getHomework().getIdHomework());
//        studentHomeworkDTO.setIdStudent(studentFromJwt.getIdUsers());
//
//        List<HomeworkFiles> listFiles = homeworkFilesRepository.findAllByHomework(studentHomework.getHomework());
//        for(HomeworkFiles homeworkFiles : listFiles){
//            studentHomeworkDTO.getIdHomeworkFiles().add(homeworkFiles.getIdHomeworkFiles());
//        }
//
//        HomeworkAnnouncements homeworkAnnouncements = studentHomework.getHomeworkAnnouncements();
//        studentHomeworkDTO.setHomeworkName(homeworkAnnouncements.getTitle());
//
//        Homework homework = studentHomework.getHomework();
//        studentHomeworkDTO.setGrade(homework.getGrade());
//
//        return studentHomeworkDTO;


    }

    @Override
    public UUID getIdHomework(UUID idHomeworkAnnouncement) {
        //vreau sa verific daca studentul a postat homework-ul
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
        Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));

        //verific daca a postat homework-ul
        StudentHomework studentHomework = studentHomeworkRepository.findByIdStudentAndIdHomeworkAnnouncement(id, idHomeworkAnnouncement).orElse(null);

        if(studentHomework == null){
            //daca nu a postat homework-ul
            System.out.println("Student homework not found");
            return UUID.fromString("00000000-0000-0000-0000-000000000001");
        }

        //daca a postat homework-ul
        return studentHomework.getHomework().getIdHomework();
    }
}
