package com.example.licentav1.mapper;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.CoursesDTO;
import com.example.licentav1.dto.LecturesCreationDTO;
import org.springframework.stereotype.Component;

@Component
public class LecturesMapper {


    public static Lectures fromDTO(LecturesCreationDTO lecturesCreationDTO, Courses courses) {
        return Lectures.builder()
                .name(lecturesCreationDTO.getName())
                .description(lecturesCreationDTO.getDescription())
                .week(lecturesCreationDTO.getWeek())
                .semester(courses.getSemester())
                .year(courses.getYear())
                .courses(courses)
                .build();
    }
}
