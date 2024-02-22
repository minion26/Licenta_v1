package com.example.licentav1.mapper;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.dto.CoursesCreationDTO;
import com.example.licentav1.dto.CoursesDTO;
import org.springframework.stereotype.Component;

@Component
public class CoursesMapper {
    public static CoursesDTO toDTO(Courses courses) {
        return CoursesDTO.builder()
                .idCourses(courses.getIdCourses())
                .name(courses.getName())
                .year(courses.getYear())
                .semester(courses.getSemester())
                .credits(courses.getCredits())
                .description(courses.getDescription())
                .build();
    }

    public static Courses fromDTO(CoursesCreationDTO coursesCreationDTO) {
        return Courses.builder()
                .name(coursesCreationDTO.getName())
                .year(coursesCreationDTO.getYear())
                .semester(coursesCreationDTO.getSemester())
                .credits(coursesCreationDTO.getCredits())
                .description(coursesCreationDTO.getDescription())
                .build();
    }
}
