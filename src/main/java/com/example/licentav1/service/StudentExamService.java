package com.example.licentav1.service;

import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface StudentExamService {
    void createStudentExam(StudentExamCreationDTO studentExamCreationDTO);

    List<StudentExamDTO> getAllStudentByExam(UUID idExam);

    void uploadStudents(MultipartFile file, UUID idExam) throws IOException;

    void deleteStudent(UUID idStudent);

    void updateStudentExam(UUID idStudentExam,StudentExamDTO studentExamDTO);
}
