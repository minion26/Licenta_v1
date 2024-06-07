package com.example.licentav1.service;

import com.example.licentav1.dto.StudentHomeworkDTO;

import java.util.List;
import java.util.UUID;

public interface StudentHomeworkService {
    Boolean checkPost(UUID idHomeworkAnnouncement, UUID idStudent);

    List<StudentHomeworkDTO> getAllByStudent();
}
