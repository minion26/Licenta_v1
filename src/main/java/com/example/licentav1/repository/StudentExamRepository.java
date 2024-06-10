package com.example.licentav1.repository;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, UUID> {

    @Query(value="SELECT * FROM student_exam WHERE id_exam = :idExam", nativeQuery = true)
    List<StudentExam> findAllStudentsByExam(@Param("idExam") UUID idExam);

//    @Query(value="SELECT * FROM student_exam WHERE id_student = :idStudent", nativeQuery = true)
//    Optional<StudentExam> findByIdStudent(@Param("idStudent") UUID idStudent);

    @Query(value="SELECT * FROM student_exam WHERE id_student = :idStudent AND id_exam = :idExam", nativeQuery = true)
    Optional<StudentExam> findByIdStudentAndIdExam(@Param("idStudent") UUID idStudent,@Param("idExam") UUID idExam);

    @Query(value="SELECT * FROM student_exam WHERE id_exam = :idExam", nativeQuery = true)
    List<StudentExam> findAllByIdExam(@Param("idExam") UUID idExam);
}
