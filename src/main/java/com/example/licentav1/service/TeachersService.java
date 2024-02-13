package com.example.licentav1.service;

import com.example.licentav1.domain.Teachers;
import com.example.licentav1.dto.TeachersCreationDTO;

public interface TeachersService {
    Iterable<Teachers> getTeachers();

    void createTeacher(TeachersCreationDTO teachersCreationDTO);
}
