package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.advice.exceptions.StudentExamNotFoundException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.StudentExamCreationDTO;
import com.example.licentav1.dto.StudentExamDTO;
import com.example.licentav1.dto.StudentExamFrontDTO;
import com.example.licentav1.mapper.StudentExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.StudentExamService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentsRepository studentsRepository;
    private final ExamRepository examRepository;
    private final StudentAnswersExamRepository studentAnswersExamRepository;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private final UsersRepository usersRepository;

    public StudentExamServiceImpl(StudentExamRepository studentExamRepository, StudentsRepository studentsRepository, ExamRepository examRepository, StudentAnswersExamRepository studentAnswersExamRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository, UsersRepository usersRepository) {
        this.studentExamRepository = studentExamRepository;
        this.studentsRepository = studentsRepository;
        this.examRepository = examRepository;
        this.studentAnswersExamRepository = studentAnswersExamRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void createStudentExam(StudentExamCreationDTO studentExamCreationDTO) {
        //retireve the student and the exam from the database
        Students student = studentsRepository.findById(studentExamCreationDTO.getIdStudent()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Exam exam = examRepository.findById(studentExamCreationDTO.getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        // check if the student is enrolled in the course
        StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(student.getIdUsers(), exam.getCourse().getIdCourses()).orElseThrow(() -> new StudentNotFoundException("Student not enrolled in the course"));


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

    @Override
    public void uploadStudents(MultipartFile file, UUID idExam) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String line;
        //find the exam by id
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            StudentExam studentsExam = new StudentExam();
            //studentsExamCreationDTO.setIdStudent(UUID.fromString(data[0]));
            //find the student
            Students student = studentsRepository.findByNrMatriculation(data[0]).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            //set the student to the student-exam
            studentsExam.setStudent(student);
            //set the exam to the student-exam
            studentsExam.setExam(exam);
            //set the score to the student-exam
            studentsExam.setScore(-1);
            //set the exam status
            studentsExam.setExamStatus(-1);
            //save the student-exam to the database
            studentExamRepository.save(studentsExam);

        }
    }

    @Override
    public void deleteStudent(UUID idStudentExam) {
        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The row student-exam not found"));

        // for each row that has the id of the student exam, delete the row
        studentAnswersExamRepository.deleteAll(studentAnswersExamRepository.findAllByStudentExam(studentExam.getIdStudentExam()));

        //delete the student-exam row from the database
        studentExamRepository.delete(studentExam);
    }

    @Override
    public void updateStudentExam(UUID idStudentExam,StudentExamDTO studentExamDTO) {
        // Find the student exam entry
        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The student or the exam not found"));

        //update the fields
        if (studentExamDTO.getIdExam() != null){
            Exam exam = examRepository.findById(studentExamDTO.getIdExam()).orElseThrow(() -> new ExamNotFoundException("Exam not found"));
            studentExam.setExam(exam);
        }
        if (studentExamDTO.getIdStudent() != null){
            Students student = studentsRepository.findById(studentExamDTO.getIdStudent()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
            studentExam.setStudent(student);
        }
        if (studentExamDTO.getScore() != null){
            studentExam.setScore(studentExamDTO.getScore());
        }
        if (studentExamDTO.getExamStatus() != null){
            studentExam.setExamStatus(studentExamDTO.getExamStatus());
        }

        //save the updated entry to the database
        studentExamRepository.save(studentExam);

    }

    @Override
    public StudentExamFrontDTO getStudentExamById(UUID idStudentExam) {
        StudentExam studentExam = studentExamRepository.findById(idStudentExam).orElseThrow(() -> new StudentExamNotFoundException("The student or the exam not found"));

        Students student = studentsRepository.findById(studentExam.getStudent().getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));
        Users users = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new StudentNotFoundException("Student not found"));

        return StudentExamFrontDTO.builder()
                .idStudentExam(studentExam.getIdStudentExam())
                .idStudent(studentExam.getStudent().getIdUsers())
                .studentName(users.getFirstName() + " " + users.getLastName())
                .idExam(studentExam.getExam().getIdExam())
                .score(studentExam.getScore())
                .examStatus(studentExam.getExamStatus())
                .build();
    }


}
