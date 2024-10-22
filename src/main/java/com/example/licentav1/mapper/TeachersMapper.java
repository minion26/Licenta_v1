package com.example.licentav1.mapper;

import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.TeachersDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TeachersMapper {
    public static TeachersDTO toDTO(Users users, Teachers teachers) {
        return TeachersDTO.builder()
                .idUsers(users.getIdUsers())
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

    public static Teachers fromCsvData(String[] data, UUID idUser){
        return Teachers.builder()
                .idUsers(idUser)
                .idTeacher(data[4])
                .degree(data[5])
                .build();
    }
}
