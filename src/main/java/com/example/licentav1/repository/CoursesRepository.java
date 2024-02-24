package com.example.licentav1.repository;

import com.example.licentav1.domain.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoursesRepository extends JpaRepository<Courses, UUID>{
    @Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Courses c WHERE c.name = :name", nativeQuery = true)
    boolean existsByCourseName(@Param("name") String name);

    @Query(value = "SELECT * FROM Courses c WHERE c.name = :courseName", nativeQuery = true)
    Optional<Courses> findByName(@Param("courseName") String courseName);
}
