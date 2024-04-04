package com.example.licentav1.repository;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, UUID> {

    @Query(value="SELECT * FROM student_exam WHERE id_exam = :idExam", nativeQuery = true)
    List<StudentExam> findAllStudentsByExam(UUID idExam);
}
