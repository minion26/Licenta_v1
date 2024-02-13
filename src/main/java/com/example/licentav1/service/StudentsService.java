package com.example.licentav1.service;

import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;

public interface StudentsService {
    Iterable<Students> getStudents();

    void createStudent(StudentsCreationDTO studentsCreationDTO);
}
