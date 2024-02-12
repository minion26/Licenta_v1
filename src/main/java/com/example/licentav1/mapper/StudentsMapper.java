package com.example.licentav1.mapper;

import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import org.springframework.stereotype.Component;

@Component
public class StudentsMapper {

    public StudentsDTO toDTO(Users users, Students students) {
        return StudentsDTO.builder()
                .lastName(users.getLastName())
                .firstName(users.getFirstName())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .nrMatriculation(students.getNrMatriculation())
                .yearOfStudy(students.getYearOfStudy())
                .semester(students.getSemester())
                .groupOfStudy(students.getGroupOfStudy())
                .enrollmentDate(students.getEnrollmentDate())
                .build();
    }

    public Students fromDTO(StudentsDTO studentsDto) {
        return Students.builder()
                .nrMatriculation(studentsDto.getNrMatriculation())
                .yearOfStudy(studentsDto.getYearOfStudy())
                .semester(studentsDto.getSemester())
                .groupOfStudy(studentsDto.getGroupOfStudy())
                .enrollmentDate(studentsDto.getEnrollmentDate())
                .build();
    }


}
