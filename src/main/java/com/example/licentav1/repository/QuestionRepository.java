package com.example.licentav1.repository;

import com.example.licentav1.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    @Query(value="SELECT * FROM questions WHERE id_exam = :idExam", nativeQuery = true)
    List<Question> findAllByIdExam(@Param("idExam") UUID idExam);

}
