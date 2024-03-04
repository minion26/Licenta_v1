package com.example.licentav1.mapper;

import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StudentsMapper {

    public static StudentsDTO toDTO(Users users, Students students) {
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

    public static Students fromCsvData(String[] data, UUID idUser){
        return Students.builder()
                .idUsers(idUser)
                .nrMatriculation(data[4])
                .yearOfStudy(Integer.parseInt(data[5]))
                .semester(Integer.parseInt(data[6]))
                .groupOfStudy(data[7])
                .enrollmentDate(java.time.LocalDateTime.now())
                .build();
    }


}
