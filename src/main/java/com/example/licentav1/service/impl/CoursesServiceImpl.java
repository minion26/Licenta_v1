package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.mapper.CoursesMapper;
import com.example.licentav1.mapper.TeachersMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.CoursesService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CoursesServiceImpl implements CoursesService {
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final TeachersRepository teacherRepository;
    private final UsersRepository usersRepository;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;


    public CoursesServiceImpl(CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teacherRepository, UsersRepository usersRepository, HttpServletRequest request, JwtService jwtService, StudentsFollowCoursesRepository studentsFollowCoursesRepository) {
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teacherRepository = teacherRepository;
        this.usersRepository = usersRepository;
        this.request = request;
        this.jwtService = jwtService;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
    }


    @Override
    public void createCourse(CoursesCreationDTO coursesCreationDTO) {
        if (coursesRepository.existsByCourseName(coursesCreationDTO.getName())) {
            throw new CourseAlreadyExistsException("Course already exists");
        }

        coursesRepository.save(CoursesMapper.fromDTO(coursesCreationDTO));

    }

    @Override
    public List<CoursesDTO> getCourses() {
        return coursesRepository.findAll().stream().map(CoursesMapper::toDTO).toList();
    }

    @Override
    public CoursesDTO getCourseById(UUID id) {
        Courses courses = coursesRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        return CoursesMapper.toDTO(courses);
    }

    @Override
    public List<CoursesDTO> getCoursesByTeacher(UUID idTeacher) {
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
//        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));


        //profu cu id din params
        Teachers teacher = teacherRepository.findById(idTeacher).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        List<Didactic> didacticList = didacticRepository.findAllByIdTeacher(idTeacher).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        for (Didactic didactic : didacticList) {
            if (! didactic.getTeachers().getIdUsers().equals(teacherFromJwt.getIdUsers())) {
                throw new NonAllowedException("You are not allowed to see this course");
            }
        }

        return didacticList.stream().map(Didactic::getCourses).map(CoursesMapper::toDTO).toList();
    }

    @Override
    public List<TeachersDTO> getTeachersByCourse(UUID idCourse) {
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
//        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teacherRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));


        Courses courses = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        //vad daca profu preda la cursul respectiv
        Didactic didactics = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), idCourse).orElse(null);

        if (didactics == null) {
            throw new NonAllowedException("You are not allowed to see this course");
        }
//        else{
//            System.out.println("Profu preda la cursul respectiv");
//        }

        List<Didactic> didacticList = didacticRepository.findAllByIdCourse(idCourse).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        List<TeachersDTO> teachersDtos = new ArrayList<>();
        for (Didactic didactic : didacticList) {
            Teachers teacher = teacherRepository.findById(didactic.getTeachers().getIdUsers()).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
            Users user = usersRepository.findById(teacher.getIdUsers()).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
            TeachersDTO teachersDTO = TeachersMapper.toDTO(user, teacher);

            teachersDtos.add(teachersDTO);
        }

        return teachersDtos;
    }

    @Override
    public List<CoursesDTO> getCoursesForStudent(UUID idStudent, Integer semester) {
       //vreau sa verific daca studentul urmeaza cursul respectiv
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
//        System.out.println("id from token: " + id);
        Users userFromJwt = usersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        //studentul cu id din params
        Users user = usersRepository.findById(idStudent).orElse(null);

        if (user == null) {
            throw new NonAllowedException("You are not allowed to see this course");
        }

        List<StudentsFollowCourses> studentsFollowCoursesList = studentsFollowCoursesRepository.findAllByIdStudent(idStudent).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        List<CoursesDTO> coursesDtos = new ArrayList<>();

        for (StudentsFollowCourses studentsFollowCourses : studentsFollowCoursesList) {
            Courses courses = coursesRepository.findById(studentsFollowCourses.getCourse().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found"));
            if (courses.getSemester().equals(semester)) {
                CoursesDTO coursesDTO = CoursesMapper.toDTO(courses);
                coursesDtos.add(coursesDTO);
            }

        }

        return coursesDtos;

    }

    @Override
    public void uploadCourses(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            Courses courses = CoursesMapper.fromCsvDataCourse(data);

            if (coursesRepository.existsByCourseName(courses.getName())) {
                throw new CourseAlreadyExistsException("Course already exists");
            }

            coursesRepository.save(courses);
        }
    }

    @Override
    public void deleteCourse(UUID id) throws CourseNotFoundException {
        //delete the course by id
        Courses courses = coursesRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        coursesRepository.delete(courses);

    }

    @Override
    public void updateCourse(UUID id, CoursesDTO coursesDTO) {
        Courses courses = coursesRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        if (coursesDTO.getName() != null) {
            courses.setName(coursesDTO.getName());
        }

        if (coursesDTO.getYear() != null) {
            courses.setYear(coursesDTO.getYear());
        }

        if (coursesDTO.getSemester() != null) {
            courses.setSemester(coursesDTO.getSemester());
        }

        if (coursesDTO.getCredits() != null) {
            courses.setCredits(coursesDTO.getCredits());
        }

        if (coursesDTO.getDescription() != null) {
            courses.setDescription(coursesDTO.getDescription());
        }

        coursesRepository.save(courses);
    }
}
