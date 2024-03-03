package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.mapper.LecturesMapper;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.LecturesRepository;
import com.example.licentav1.service.LecturesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LecturesServiceImpl implements LecturesService {
    private LecturesRepository lecturesRepository;
    private CoursesRepository coursesRepository;

    public LecturesServiceImpl(LecturesRepository lecturesRepository, CoursesRepository coursesRepository) {
        this.lecturesRepository = lecturesRepository;
        this.coursesRepository = coursesRepository;
    }
    @Override
    public List<Lectures> getLectures() {
        return lecturesRepository.getLectures();
    }

    @Override
    public void createLecture(LecturesCreationDTO lecturesCreationDTO, UUID idCourse) {
        Courses course = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        Lectures lecture;

        try{
            lecture = LecturesMapper.fromDTO(lecturesCreationDTO, course);
            System.out.println(lecture.getIdLecture());
            System.out.println(lecture.getName());
            System.out.println(lecture.getDescription());
            System.out.println(lecture.getWeek());
            System.out.println(lecture.getSemester());
            System.out.println(lecture.getYear());

            if (lecture != null) {
                lecturesRepository.save(lecture);
            }
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }
}
