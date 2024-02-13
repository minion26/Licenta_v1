package com.example.licentav1.mapper;

import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.dto.UsersDTO;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {
    public UsersDTO toDto(Users users) {

        return UsersDTO.builder()
                .idUsers(users.getIdUsers())
                .password(users.getPassword())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .roleId(users.getRoleId())
                .build();
    }

    public static Users fromDto(UsersDTO usersDto) {
        return Users.builder()
                .idUsers(usersDto.getIdUsers())
                .firstName(usersDto.getFirstName())
                .lastName(usersDto.getLastName())
                .password(usersDto.getPassword())
                .facultyEmail(usersDto.getFacultyEmail())
                .personalEmail(usersDto.getPersonalEmail())
                .roleId(usersDto.getRoleId())
                .build();
    }

    public static Users fromStudentCreationDTO(StudentsCreationDTO studentsCreationDTO) {
        return Users.builder()
                .firstName(studentsCreationDTO.getFirstName())
                .lastName(studentsCreationDTO.getLastName())
                .facultyEmail(studentsCreationDTO.getFacultyEmail())
                .personalEmail(studentsCreationDTO.getPersonalEmail())
                .password(studentsCreationDTO.getPassword())
                .roleId(3)
                .build();
    }

    public UsersDTO fromStudentDTO(Users users) {
        return UsersDTO.builder()
                .idUsers(users.getIdUsers())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .roleId(users.getRoleId())
                .build();
    }

    public static Users fromTeacherCreationDTO(TeachersCreationDTO teachersCreationDTO) {
        return Users.builder()
                .firstName(teachersCreationDTO.getFirstName())
                .lastName(teachersCreationDTO.getLastName())
                .facultyEmail(teachersCreationDTO.getFacultyEmail())
                .personalEmail(teachersCreationDTO.getPersonalEmail())
                .password(teachersCreationDTO.getPassword())
                .roleId(2)
                .build();
    }
}
