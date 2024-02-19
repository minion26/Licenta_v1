package com.example.licentav1.service;

import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;

import java.util.UUID;

public interface StudentsService {
    Iterable<StudentsDTO> getStudents();

    void createStudent(StudentsCreationDTO studentsCreationDTO);

    void updateStudent(UUID id, StudentsDTO studentsDTO);

    void deleteStudent(UUID id);
}
