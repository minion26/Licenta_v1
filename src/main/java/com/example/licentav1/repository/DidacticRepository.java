package com.example.licentav1.repository;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Didactic;
import com.example.licentav1.domain.Teachers;
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

    @Query(value="SELECT * FROM didactic WHERE id_teacher = :idTeacher", nativeQuery = true)
    Optional<List<Didactic>> findAllByIdTeacher(UUID idTeacher);

    @Query(value="SELECT * FROM didactic WHERE id_courses = :idCourse", nativeQuery = true)
    Optional<List<Didactic>> findAllByIdCourse(UUID idCourse);

    @Query(value="SELECT * FROM didactic WHERE id_courses = :idCourse AND id_teacher = :idUsers", nativeQuery = true)
    Optional<Didactic> findByTeacherAndCourse(@Param("idUsers") UUID idUsers,@Param("idCourse") UUID idCourses);

    @Query(value="SELECT * FROM didactic WHERE id_teacher = :idTeacher", nativeQuery = true)
    Optional<List<Didactic>> findAllByIdTeachers(UUID idTeacher);
}
