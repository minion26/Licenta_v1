package com.example.licentav1.repository;

import com.example.licentav1.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {

    @Query(value="SELECT * FROM exam WHERE course_id = :idCourse", nativeQuery = true)
    Optional<List<Exam>> findAllByCourses(UUID idCourse);
}
