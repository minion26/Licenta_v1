package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.StudentCourseRelationNotFoundException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.StudentsFollowCourses;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsFollowCoursesDTO;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.StudentsFollowCoursesRepository;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.StudentsFollowCoursesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentsFollowCoursesServiceImpl implements StudentsFollowCoursesService {
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;
    private final CoursesRepository coursesRepository;

    public StudentsFollowCoursesServiceImpl(StudentsFollowCoursesRepository studentsFollowCoursesRepository, StudentsRepository studentsRepository, UsersRepository usersRepository, CoursesRepository coursesRepository) {
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
        this.coursesRepository = coursesRepository;
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException,StudentNotFoundException,CourseNotFoundException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String studentNrMatriculation = data[0];
            String courseName = data[1];

            Optional<Students> student = studentsRepository.findByNrMatriculation(studentNrMatriculation);
            Optional<Courses> course = coursesRepository.findByName(courseName);

            if (student.isEmpty()) {
                throw new StudentNotFoundException("Student not found");
            }
            if (course.isEmpty()){
                throw new CourseNotFoundException("Course not found");
            }

            StudentsFollowCourses studentsFollowCourses = new StudentsFollowCourses(student.get(), course.get());


            try {
                studentsFollowCoursesRepository.save(studentsFollowCourses);
            } catch (Exception e) {
                System.out.printf("Error: %s", e.getMessage());
            }

        }
    }

    @Override
    public List<StudentsFollowCoursesDTO> getAllStudentsFollowCourses() {
        return studentsFollowCoursesRepository.findAll().stream()
                .map(std -> {
                    StudentsFollowCoursesDTO studentsFollowCoursesDTO = new StudentsFollowCoursesDTO();

                    //Set the id of the student-course relation
                    studentsFollowCoursesDTO.setIdStudentFollowCourse(std.getIdStudentsFollowCourses());

                    //Fetch the student and course
                    Students student = studentsRepository.findById(std.getStudent().getIdUsers()).orElse(null);
                    Courses course = coursesRepository.findById(std.getCourse().getIdCourses()).orElse(null);

                    //Set the student and course name in the DTO
                    if (student != null) {
                        Users user = usersRepository.findById(student.getIdUsers()).orElse(null);

                        if (user != null) {
                            studentsFollowCoursesDTO.setStudentName(user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    if (course != null) {
                        studentsFollowCoursesDTO.setCourseName(course.getName());
                    }

                    return studentsFollowCoursesDTO;

                }).collect(Collectors.toList());
    }



    @Override
    public void deleteStudentFollowCourse(String id) throws StudentCourseRelationNotFoundException{
        StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findById(UUID.fromString(id)).orElseThrow(() -> new StudentCourseRelationNotFoundException("Student-Course relation not found"));
        studentsFollowCoursesRepository.delete(studentsFollowCourses);
    }

    @Override
    public void updateStudentFollowCourse(String id, StudentsFollowCoursesDTO studentsFollowCoursesDTO) {
        StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findById(UUID.fromString(id)).orElseThrow(() -> new StudentCourseRelationNotFoundException("Student-Course relation not found"));

        if (studentsFollowCoursesDTO.getStudentName() != null) {
            String[] nameParts = studentsFollowCoursesDTO.getStudentName().split(" ");
            String firstName = nameParts[0];
            String lastName = nameParts[1];

            UUID studentId = studentsRepository.findByName(firstName, lastName);
            Students student = studentsRepository.findById(studentId).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            studentsFollowCourses.setStudent(student);
        }

        if (studentsFollowCoursesDTO.getCourseName() != null) {
            Courses course = coursesRepository.findByName(studentsFollowCoursesDTO.getCourseName()).orElseThrow(() -> new CourseNotFoundException("Course not found"));
            studentsFollowCourses.setCourse(course);
        }

        try {
            studentsFollowCoursesRepository.save(studentsFollowCourses);
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }

    }

    @Override
    public List<StudentsFollowCoursesDTO> getStudentFollowCourse(String courseName) {
        Courses courses = coursesRepository.findByName(courseName).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        UUID courseId = courses.getIdCourses();
//        System.out.println("id: " + courseId);
        return studentsFollowCoursesRepository.findAllByCourse(courseId).orElse(Collections.emptyList()).stream()
                .map(std -> {
                    StudentsFollowCoursesDTO studentsFollowCoursesDTO = new StudentsFollowCoursesDTO();

                    studentsFollowCoursesDTO.setIdStudentFollowCourse(std.getIdStudentsFollowCourses());

                    Students student = studentsRepository.findById(std.getStudent().getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
                    Courses course = coursesRepository.findById(std.getCourse().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found"));

                    if (student != null) {
                        Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new UserNotFoundException("User not found"));

                        if (user != null) {
                            studentsFollowCoursesDTO.setStudentName(user.getFirstName() + " " + user.getLastName());
                        }
                    }

                    if (course != null) {
                        studentsFollowCoursesDTO.setCourseName(course.getName());
                    }

//                    System.out.println(studentsFollowCoursesDTO.getStudentName() + " " + studentsFollowCoursesDTO.getCourseName());
                    return studentsFollowCoursesDTO;

                }).collect(Collectors.toList());
    }
}
