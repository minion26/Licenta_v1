package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.dto.LecturesDTO;
import com.example.licentav1.mapper.LecturesMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.LecturesService;
import com.example.licentav1.service.MaterialsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LecturesServiceImpl implements LecturesService {
    private final LecturesRepository lecturesRepository;
    private final CoursesRepository coursesRepository;
    private final MaterialsRepository materialsRepository;
    private final MaterialsService materialsService;
    private final JwtService jwtService;
    private final HttpServletRequest request;
    private final TeachersRepository teacherRepository;
    private final DidacticRepository didacticRepository;
    private final StudentsRepository studentsRepository;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;

    public LecturesServiceImpl(LecturesRepository lecturesRepository, CoursesRepository coursesRepository, MaterialsRepository materialsRepository, MaterialsService materialsService, JwtService jwtService, HttpServletRequest request, TeachersRepository teacherRepository, DidacticRepository didacticRepository, StudentsRepository studentsRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository) {
        this.lecturesRepository = lecturesRepository;
        this.coursesRepository = coursesRepository;
        this.materialsRepository = materialsRepository;
        this.materialsService = materialsService;
        this.jwtService = jwtService;
        this.request = request;
        this.teacherRepository = teacherRepository;
        this.didacticRepository = didacticRepository;
        this.studentsRepository = studentsRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
    }
    @Override
    public List<LecturesDTO> getLectures() {
        ArrayList<Lectures> lectures = (ArrayList<Lectures>) lecturesRepository.findAll();
        ArrayList<Courses> courses = (ArrayList<Courses>) coursesRepository.findAll();
        ArrayList<LecturesDTO> lecturesDTO = new ArrayList<>();

        for (Lectures lecture : lectures) {
            for (Courses course : courses) {
                if (lecture.getCourses().getIdCourses().equals(course.getIdCourses())) {
                    lecturesDTO.add(LecturesMapper.toDTO(lecture, course));
                }
            }
        }
        return lecturesDTO;
    }

    @Override
    public void createLecture(LecturesCreationDTO lecturesCreationDTO, UUID idCourse) {
        Courses course = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        Lectures lecture;

        try{
            lecture = LecturesMapper.fromDTO(lecturesCreationDTO, course);
//            System.out.println(lecture.getIdLecture());
//            System.out.println(lecture.getName());
//            System.out.println(lecture.getDescription());
//            System.out.println(lecture.getWeek());
//            System.out.println(lecture.getSemester());
//            System.out.println(lecture.getYear());

            if (lecture != null) {
                lecturesRepository.save(lecture);
            }
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }

    @Override
    public void deleteLecture(UUID idLecture) {
        Lectures lecture = lecturesRepository.findById(idLecture).orElseThrow(() -> new CourseNotFoundException("Lecture not found"));

        List<Materials> materials = lecture.getMaterials();
        for (Materials material : materials) {
            //delete from s3
            materialsService.deleteFile(material.getIdMaterial());
            materialsRepository.delete(material);

        }

        lecturesRepository.delete(lecture);
    }

    @Override
    public void updateLecture(LecturesDTO lecturesDTO, UUID idLecture) {
        // Fetch and update the Lectures entity
        Lectures lecture = lecturesRepository.findById(idLecture).orElseThrow(() -> new CourseNotFoundException("Lecture not found"));

        if (lecturesDTO.getName() != null) {
            lecture.setName(lecturesDTO.getName());
        }
        if (lecturesDTO.getDescription() != null) {
            lecture.setDescription(lecturesDTO.getDescription());
        }
        if (lecturesDTO.getWeek() != null) {
            lecture.setWeek(lecturesDTO.getWeek());
        }
        if (lecturesDTO.getSemester() != null) {
            lecture.setSemester(lecturesDTO.getSemester());
        }
        if (lecturesDTO.getYear() != null) {
            lecture.setYear(lecturesDTO.getYear());
        }

        lecturesRepository.save(lecture);
    }

    @Override
    public LecturesDTO getLecture(UUID idLecture) {
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
        String role = jwtService.extractRole(token);
        System.out.println("id from token: " + idToken);

        Lectures lecture = lecturesRepository.findById(idLecture).orElseThrow(() -> new CourseNotFoundException("Lecture not found"));
        Courses course = coursesRepository.findById(lecture.getCourses().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

            //verific daca profesorul preda la cursul respectiv
            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(didactic == null) {
                throw new NonAllowedException("You are not allowed to see this course");
            }else{
                System.out.println("You are allowed to see this course");
            }
        }else if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(idToken).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            //verific daca studentul este inscris la cursul respectiv
            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to see this course");
            }
        }else{
            //inseamna ca e admin
            throw new NonAllowedException("You are not allowed to see this course");
        }


        return LecturesMapper.toDTO(lecture, course);
    }

    @Override
    public List<LecturesDTO> getLecturesByCourse(UUID idCourses) {
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
        String role = jwtService.extractRole(token);
        System.out.println("id from token: " + idToken);

        Courses course = coursesRepository.findById(idCourses).orElseThrow(()-> new CourseNotFoundException("Course not found"));

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));


            //verific daca profesorul preda la cursul respectiv
            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), idCourses).orElse(null);

            if (didactic == null) {
                throw new NonAllowedException("You are not allowed to see this course");
            }else{
                System.out.println("You are allowed to see this course");
            }
        }else if (role.equals("STUDENT")){
            //verific daca studentul este inscris la cursul respectiv
            Students studentFromJwt = studentsRepository.findById(idToken).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), idCourses).orElse(null);

            if (studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to see this course");
            }else{
                System.out.println("You are allowed to see this course");
            }
        }else{
            //inseamna ca e admin
            throw new NonAllowedException("You are not allowed to see this course");
        }


        List<Lectures> lectures = lecturesRepository.findByIdCourses(idCourses);

        List<LecturesDTO> lecturesDTO = new ArrayList<>();
        for (Lectures lecture : lectures) {
            lecturesDTO.add(LecturesMapper.toDTO(lecture, course));
        }
        return lecturesDTO;
    }


}
