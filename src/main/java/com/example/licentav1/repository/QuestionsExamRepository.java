package com.example.licentav1.repository;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.QuestionsExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionsExamRepository extends JpaRepository<QuestionsExam, UUID> {
    @Query(value="SELECT * FROM questions_exam qe WHERE qe.id_exam = :idExam", nativeQuery = true)
    List<QuestionsExam> findAllByIdExam(@Param("idExam") UUID idExam);

    @Query(value="SELECT * FROM questions_exam qe WHERE qe.question = :idQuestion AND qe.id_exam = :idExam", nativeQuery = true)
    Optional<QuestionsExam> findByIdQuestionAndIdExam(@Param("idQuestion") UUID idQuestion,@Param("idExam") UUID idExam);

    @Query(value="SELECT * FROM questions_exam qe WHERE qe.question = :idQuestion", nativeQuery = true)
    Optional<QuestionsExam> findByIdQuestion(UUID idQuestion);

    @Query(value="SELECT * FROM questions_exam qe WHERE qe.id_exam = :idExam", nativeQuery = true)
    List<QuestionsExam> findByIdExam(UUID idExam);
}
