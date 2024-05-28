package com.example.licentav1.repository;

import com.example.licentav1.domain.Didactic;
import jakarta.validation.constraints.Past;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DidacticRepository extends JpaRepository<Didactic, UUID> {
    @Query(value="SELECT * FROM didactic WHERE id_courses = :idCourses", nativeQuery = true)
    Optional<Didactic> findByIdCourses(@Param("idCourses") UUID idCourses);

    @Query(value="SELECT * FROM didactic WHERE id_courses = :idCourses", nativeQuery = true)
    Optional<List<Didactic>> findAllByIdCourses(@Param("idCourses") UUID idCourses);

}
