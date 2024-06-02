package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Didactic;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.mapper.CoursesMapper;
import com.example.licentav1.mapper.TeachersMapper;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.DidacticRepository;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.CoursesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CoursesServiceImpl implements CoursesService {
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final TeachersRepository teacherRepository;
    private final UsersRepository usersRepository;

    public CoursesServiceImpl(CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teacherRepository, UsersRepository usersRepository) {
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teacherRepository = teacherRepository;
        this.usersRepository = usersRepository;
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
        Teachers teacher = teacherRepository.findById(idTeacher).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        List<Didactic> didacticList = didacticRepository.findAllByIdTeacher(idTeacher).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        return didacticList.stream().map(Didactic::getCourses).map(CoursesMapper::toDTO).toList();
    }

    @Override
    public List<TeachersDTO> getTeachersByCourse(UUID idCourse) {
        Courses courses = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));
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
