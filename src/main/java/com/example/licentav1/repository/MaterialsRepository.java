package com.example.licentav1.repository;

import com.example.licentav1.domain.Lectures;
import com.example.licentav1.domain.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, UUID> {
    @Query(value = "SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Materials m WHERE m.id_lecture = :idLecture AND m.name = :fileName", nativeQuery = true)
    boolean existsByLecturesAndName(@Param("idLecture") UUID idLecture,@Param("fileName") String fileName);
}
