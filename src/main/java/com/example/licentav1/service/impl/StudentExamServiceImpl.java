package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.StudentExam;
import com.example.licentav1.domain.Students;
import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import com.example.licentav1.mapper.StudentExamMapper;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.StudentExamRepository;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.service.StudentExamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    public final StudentExamRepository studentExamRepository;
    public final StudentsRepository studentsRepository;
    public final ExamRepository examRepository;

    public StudentExamServiceImpl(StudentExamRepository studentExamRepository, StudentsRepository studentsRepository, ExamRepository examRepository) {
        this.studentExamRepository = studentExamRepository;
        this.studentsRepository = studentsRepository;
        this.examRepository = examRepository;
    }

    @Override
    public void createStudentExam(StudentExamCreationDTO studentExamCreationDTO) {
        Students student = studentsRepository.findById(studentExamCreationDTO.getIdStudent()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Exam exam = examRepository.findById(studentExamCreationDTO.getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        StudentExam studentExam = StudentExamMapper.fromDTO(studentExamCreationDTO, student, exam);

        studentExamRepository.save(studentExam);

    }

    @Override
    public List<StudentExamDTO> getAllStudentByExam(UUID idExam) {
        List<StudentExam> studentExams = studentExamRepository.findAllStudentsByExam(idExam);
        List<StudentExamDTO> studentExamDTOS = new ArrayList<>();
        for (StudentExam studentExam : studentExams) {
            studentExamDTOS.add(StudentExamMapper.toDTO(studentExam));
        }

        return studentExamDTOS;
    }


}
