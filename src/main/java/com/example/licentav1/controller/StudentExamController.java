package com.example.licentav1.controller;

import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import com.example.licentav1.dto.StudentExamFrontDTO;
import com.example.licentav1.service.StudentExamService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/student-exam")
public class StudentExamController {
    private final StudentExamService studentExamService;

    public StudentExamController(StudentExamService studentExamService) {
        this.studentExamService = studentExamService;
    }

    @GetMapping("/idExam={idExam}")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentExamDTO> getAllStudentByExams(@PathVariable UUID idExam) {
        return studentExamService.getAllStudentByExam(idExam);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    // se creaza un entry pentru tabela student_exam
    public void createStudentExam(@RequestBody StudentExamCreationDTO studentExamCreationDTO) {
        studentExamService.createStudentExam(studentExamCreationDTO);
    }

    @PostMapping("/upload/idExam={idExam}")
    @ResponseStatus(HttpStatus.CREATED)
    // se uploadeaza studentii care urmeaza sa dea examenul
    public void uploadStudents(@RequestParam("file") MultipartFile file, @PathVariable UUID idExam) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }
        studentExamService.uploadStudents(file, idExam);
    }

    @DeleteMapping("/delete/idStudentExam={idStudentExam}")
    @ResponseStatus(HttpStatus.OK)
    // se sterge un student din tabela student_exam
    public void deleteStudent(@PathVariable UUID idStudentExam) {
        studentExamService.deleteStudent(idStudentExam);
    }

    @PatchMapping("/update/idStudentExam={idStudentExam}")
    @ResponseStatus(HttpStatus.OK)
    // se updateaza un entry din tabela student_exam
    public void updateStudentExam(@PathVariable UUID idStudentExam,@RequestBody StudentExamDTO studentExamDTO) {
        studentExamService.updateStudentExam(idStudentExam ,studentExamDTO);
    }


    @GetMapping("/get/idStudentExam={idStudentExam}")
    @ResponseStatus(HttpStatus.OK)
    public StudentExamFrontDTO getStudentExamById(@PathVariable UUID idStudentExam) {
        return studentExamService.getStudentExamById(idStudentExam);
    }

}
