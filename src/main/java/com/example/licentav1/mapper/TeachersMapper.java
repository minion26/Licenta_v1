package com.example.licentav1.mapper;

import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.TeachersDTO;
import org.springframework.stereotype.Component;

@Component
public class TeachersMapper {
    public static TeachersDTO toDTO(Users users, Teachers teachers) {
        return TeachersDTO.builder()
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .idTeacher(teachers.getIdTeacher())
                .degree(teachers.getDegree())
                .build();

    }

    public Teachers fromDTO(TeachersDTO teachersDto) {
        return Teachers.builder()
                .idTeacher(teachersDto.getIdTeacher())
                .degree(teachersDto.getDegree())
                .build();
    }
}
