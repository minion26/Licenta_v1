package com.example.licentav1.repository;

import com.example.licentav1.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    @Query(value = "SELECT * FROM feedback WHERE id_homework = :idHomework", nativeQuery = true)
    List<Feedback> findAllByIdHomeWork(@Param("idHomework") UUID idHomework);
}
