package com.example.licentav1.mapper;

import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.Role;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.dto.UsersDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsersMapper {
    public static UsersDTO toDto(Users users) {

        return UsersDTO.builder()
                .idUsers(users.getIdUsers())
                .firstName(users.getFirstName())
                .lastName(users.getLastName())
                .password(users.getPassword())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .roleId(users.getRoleId()) // roleId(Role.ADMIN.ordinal())
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
                .roleId(usersDto.getRoleId()) // roleId(Role.ADMIN.ordinal())
                .build();
    }

    public static Users fromStudentCreationDTO(StudentsCreationDTO studentsCreationDTO) {
        return Users.builder()
                .firstName(studentsCreationDTO.getFirstName())
                .lastName(studentsCreationDTO.getLastName())
                .facultyEmail(studentsCreationDTO.getFacultyEmail())
                .personalEmail(studentsCreationDTO.getPersonalEmail())
                .password(studentsCreationDTO.getPassword())
                .roleId(Role.STUDENT.ordinal())
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
                .roleId(Role.TEACHER.ordinal())
                .build();
    }

    public static Users fromCsvDataStudent(String[] data){
        String password = UUID.randomUUID().toString().substring(0, 8);
        return Users.builder()
                .firstName(data[0])
                .lastName(data[1])
                .facultyEmail(data[2])
                .personalEmail(data[3])
                .password(password)
                .roleId(Role.STUDENT.ordinal())
                .build();
    }

    public static Users fromCsvDataTeacher(String[] data){
        String password = UUID.randomUUID().toString().substring(0, 8);
        return Users.builder()
                .firstName(data[0])
                .lastName(data[1])
                .facultyEmail(data[2])
                .personalEmail(data[3])
                .password(password)
                .roleId(Role.TEACHER.ordinal())
                .build();
    }
}
