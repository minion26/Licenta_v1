package com.example.licentav1.service;

import com.example.licentav1.domain.Teachers;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.dto.TeachersDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface TeachersService {
    Iterable<TeachersDTO> getTeachers();

    void createTeacher(TeachersCreationDTO teachersCreationDTO);

    void updateTeacher(UUID id, TeachersDTO teachersDTO);

    void deleteTeacher(UUID id);

    void uploadTeachers(MultipartFile file) throws IOException;
}
