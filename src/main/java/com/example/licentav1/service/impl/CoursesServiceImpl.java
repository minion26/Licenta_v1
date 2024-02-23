package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseAlreadyExistsException;
import com.example.licentav1.domain.Courses;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.mapper.CoursesMapper;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.service.CoursesService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Service
public class CoursesServiceImpl implements CoursesService {
    private final CoursesRepository coursesRepository;

    public CoursesServiceImpl(CoursesRepository coursesRepository) {
        this.coursesRepository = coursesRepository;
    }


    @Override
    public void createCourse(CoursesCreationDTO coursesCreationDTO) {
        if (coursesRepository.existsByCourseName(coursesCreationDTO.getName())) {
            throw new CourseAlreadyExistsException("Course already exists");
        }

        try{
            coursesRepository.save(CoursesMapper.fromDTO(coursesCreationDTO));
        }catch (Exception e){
            System.out.printf("Error: %s", e.getMessage());
        }
    }

    @Override
    public List<CoursesDTO> getCourses() {
        return coursesRepository.findAll().stream().map(CoursesMapper::toDTO).toList();
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
}
