package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.DidacticRelationNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Didactic;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.DidacticDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.DidacticRepository;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.DidacticService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DidacticServiceImpl implements DidacticService {

    private final DidacticRepository didacticRepository;
    private final CoursesRepository coursesRepository;
    private final TeachersRepository teacherRepository;

    private final UsersRepository usersRepository;

    public DidacticServiceImpl(DidacticRepository didacticRepository, CoursesRepository coursesRepository, TeachersRepository teacherRepository, UsersRepository usersRepository) {
        this.didacticRepository = didacticRepository;
        this.coursesRepository = coursesRepository;
        this.teacherRepository = teacherRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void createDidactic(UUID courseId, UUID teacherId) {
        Optional<Teachers> teachers = teacherRepository.findById(teacherId);
        Optional<Courses> courses = coursesRepository.findById(courseId);

        if (teachers.isEmpty() || courses.isEmpty()) {
            throw new RuntimeException("Teacher or course not found");
        }

        Didactic didactic = new Didactic(teachers.get(), courses.get());

        try{
            didacticRepository.save(didactic);
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException, TeacherNotFoundException, CourseNotFoundException{
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String teacherId = data[0];
            String courseName = data[1];

            Optional<Teachers> teachers = teacherRepository.findByTeacherId(teacherId);
            Optional<Courses> courses = coursesRepository.findByName(courseName);

            if (teachers.isEmpty() ) {
                throw new TeacherNotFoundException("Teacher not found");
            }
            if (courses.isEmpty()){
                throw new CourseNotFoundException("Course not found");
            }

            Didactic didactic = new Didactic(teachers.get(), courses.get());

            try{
                didacticRepository.save(didactic);
            } catch (Exception e) {
                System.out.printf("Error: %s", e.getMessage());
            }
        }
    }

    @Override
    public List<DidacticDTO> getAllDidactic() {
        // Step 1: Fetch all didactic entries
        return didacticRepository.findAll().stream()
                .map(didactic -> {
                    DidacticDTO dto = new DidacticDTO();

                    dto.setIdDidactic(didactic.getIdDidactic());

                    //Step 2: Fetch the teacher and course for each didactic entry
                    Teachers teacher = teacherRepository.findById(didactic.getTeachers().getIdUsers()).orElse(null);
                    Courses course = coursesRepository.findById(didactic.getCourses().getIdCourses()).orElse(null);

                    //Step 3: Set the teacher and course names in the DTO
                    if (teacher != null) {

                        //Step 4: Fetch the user for each teacher
                        Users user = usersRepository.findById(teacher.getIdUsers()).orElse(null);

                        //Step 5: Set the teacher name in the DTO
                        if (user != null) {
                            dto.setTeacherName(user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    //Step 6: Set the course name in the DTO
                    if (course != null) {
                        dto.setCourseName(course.getName());
                    }
                    return dto;

                    //Step 7: Collect the DTOs into a list
                }).collect(Collectors.toList());

    }

    @Override
    public List<DidacticDTO> getDidacticByCourse(UUID idCourse) {
        return didacticRepository.findAllByIdCourses(idCourse).orElse(Collections.emptyList()).stream()
                .map(didactic -> {
                    DidacticDTO dto = new DidacticDTO();

                    dto.setIdDidactic(didactic.getIdDidactic());

                    Teachers teacher = teacherRepository.findById(didactic.getTeachers().getIdUsers()).orElse(null);
                    Courses course = coursesRepository.findById(didactic.getCourses().getIdCourses()).orElse(null);

                    if (teacher != null) {
                        Users user = usersRepository.findById(teacher.getIdUsers()).orElse(null);

                        if (user != null) {
                            dto.setTeacherName(user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    if (course != null) {
                        dto.setCourseName(course.getName());
                    }
                    return dto;
                }).collect(Collectors.toList());

    }


    @Override
    public void deleteDidactic(UUID id) throws DidacticRelationNotFoundException {
        Didactic didactic = didacticRepository.findById(id).orElseThrow(() -> new DidacticRelationNotFoundException("Didactic relation not found"));
        didacticRepository.delete(didactic);

    }

    @Override
    public void updateDidactic(UUID id, DidacticDTO didacticDTO) throws DidacticRelationNotFoundException, TeacherNotFoundException, CourseNotFoundException {
        Didactic didactic = didacticRepository.findById(id).orElseThrow(() -> new DidacticRelationNotFoundException("Didactic relation not found"));

        if (didacticDTO.getTeacherName() != null) {
            String[] nameParts = didacticDTO.getTeacherName().split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            UUID teacherId = teacherRepository.findByName(firstName, lastName);
            Teachers teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
            didactic.setTeachers(teacher);

        }

        if (didacticDTO.getCourseName() != null) {
            Courses course = coursesRepository.findByName(didacticDTO.getCourseName()).orElseThrow(() -> new CourseNotFoundException("Course not found"));
            didactic.setCourses(course);
        }

        didacticRepository.save(didactic);



    }
}
