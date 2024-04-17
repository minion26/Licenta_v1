package com.example.licentav1.repository;

import com.example.licentav1.domain.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, UUID> {
    @Query(value = "SELECT * FROM homework WHERE id_homework = :idHomework", nativeQuery = true)
    Optional<Homework> findByIdHomework(@Param("idHomework") UUID idHomework);
}
