package com.example.licentav1.repository;

import com.example.licentav1.domain.CorrectAnswersExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CorrectAnswersExamRepository extends JpaRepository<CorrectAnswersExam, UUID> {

    @Query(value="SELECT * FROM correct_answers_exam WHERE id_question_exam = :idQuestionsExam", nativeQuery = true)
    Optional<CorrectAnswersExam> findByIdQuestionExam(@Param("idQuestionsExam") UUID idQuestionsExam);
}
