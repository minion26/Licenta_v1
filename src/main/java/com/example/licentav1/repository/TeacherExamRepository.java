package com.example.licentav1.repository;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.TeacherExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;
@Repository
public interface TeacherExamRepository extends JpaRepository<TeacherExam, UUID> {
    @Query(value="SELECT * FROM teacher_exam te WHERE te.id_exam = :idExam", nativeQuery = true)
    List<TeacherExam> findAllByIdExam(@Param("idExam") UUID idExam);

}
