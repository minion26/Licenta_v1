package com.example.licentav1.repository;

import com.example.licentav1.domain.StudentAnswersExam;
import com.example.licentav1.domain.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentAnswersExamRepository extends JpaRepository<StudentAnswersExam, UUID> {

    @Query(value="SELECT * FROM student_answers_exam WHERE id_student_exam = :idStudentExam", nativeQuery = true)
    List<StudentAnswersExam> findAllByStudentExam(@Param("idStudentExam") UUID idStudentExam);

    @Query(value="SELECT * FROM student_answers_exam WHERE id_student_exam = :idStudentExam AND needs_review = :needsReview", nativeQuery = true)
    List<StudentAnswersExam> findAllByStudentExamAndNeedsReview(@Param("idStudentExam") UUID idStudentExam,@Param("needsReview") boolean b);
}
