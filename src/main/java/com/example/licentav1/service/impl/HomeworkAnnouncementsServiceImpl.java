package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;
import com.example.licentav1.mapper.HomeworkAnnouncementsMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.HomeworkAnnouncementsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    public HomeworkAnnouncementsServiceImpl(HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, LecturesRepository lectureRepository, CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teachersRepository, JwtService jwtService, HttpServletRequest request) {
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.lectureRepository = lectureRepository;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teachersRepository = teachersRepository;
        this.jwtService = jwtService;
        this.request = request;
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
        System.out.println("id from token: " + id);
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
        } else {
            System.out.println("Teacher is the owner of the course!");
        }

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
        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //get the lecture
        Lectures lecture = lectureRepository.findById(idLecture).orElseThrow(() -> new LectureNotFoundException("Lecture not found!"));

        //am lecture, iau course
        Courses course = lecture.getCourses();
        //am course, iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to see this homework announcement!");
        } else {
            System.out.println("You are allowed to see this homework announcement!");
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
        System.out.println("id from token: " + id);
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
        } else {
            System.out.println("You are allowed to update this homework announcement!");
        }

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
        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        HomeworkAnnouncements hA = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new LectureNotFoundException("Homework announcement not found!"));

        //am hw announcement, iau lecture
        Lectures lecture = hA.getLectures();

        //am lecture, iau course
        Courses course = lecture.getCourses();

        //am course si teacher, iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if (didactic == null) {
            throw new NonAllowedException("You are not allowed to see this homework announcement!");
        } else {
            System.out.println("You are allowed to see this homework announcement!");
        }

        return HomeworkAnnouncementsMapper.toDTO(hA);
    }
}
