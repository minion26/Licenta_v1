package com.example.licentav1.repository;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.StudentsFollowCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface StudentsFollowCoursesRepository extends JpaRepository<StudentsFollowCourses, UUID> {

    @Query(value = "SELECT * FROM students_follow_courses s WHERE s.id_student = :idUsers", nativeQuery = true)
    Optional<StudentsFollowCourses> findByIdStudent(@Param("idUsers") UUID idUsers);

    @Query(value = "SELECT * FROM students_follow_courses s WHERE s.id_course = :idCourses AND s.id_student=:idUsers", nativeQuery = true)
    Optional<StudentsFollowCourses> findByStudentAndCourse(@Param("idUsers") UUID idUsers,@Param("idCourses") UUID idCourses);

    @Query(value = "SELECT * FROM students_follow_courses s WHERE s.id_course = :idCourses", nativeQuery = true)
    Optional<List<StudentsFollowCourses>> findAllByCourse(UUID idCourses);

    @Query(value = "SELECT * FROM students_follow_courses s WHERE s.id_student = :idStudent", nativeQuery = true)
    Optional<List<StudentsFollowCourses>> findAllByIdStudent(@Param("idStudent") UUID idStudent);
}
