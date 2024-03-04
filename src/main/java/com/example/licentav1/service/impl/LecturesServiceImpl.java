package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Lectures;
import com.example.licentav1.domain.Materials;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.dto.LecturesDTO;
import com.example.licentav1.mapper.LecturesMapper;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.LecturesRepository;
import com.example.licentav1.repository.MaterialsRepository;
import com.example.licentav1.service.LecturesService;
import com.example.licentav1.service.MaterialsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LecturesServiceImpl implements LecturesService {
    private LecturesRepository lecturesRepository;
    private CoursesRepository coursesRepository;
    private MaterialsRepository materialsRepository;
    private MaterialsService materialsService;

    public LecturesServiceImpl(LecturesRepository lecturesRepository, CoursesRepository coursesRepository, MaterialsRepository materialsRepository, MaterialsService materialsService) {
        this.lecturesRepository = lecturesRepository;
        this.coursesRepository = coursesRepository;
        this.materialsRepository = materialsRepository;
        this.materialsService = materialsService;
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
        Lectures lecture = lecturesRepository.findById(idLecture).orElseThrow(() -> new CourseNotFoundException("Lecture not found"));
        Courses course = coursesRepository.findById(lecture.getCourses().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        return LecturesMapper.toDTO(lecture, course);
    }


}
