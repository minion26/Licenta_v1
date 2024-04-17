package com.example.licentav1.repository;

import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.HomeworkAnnouncements;
import com.example.licentav1.domain.StudentHomework;
import com.example.licentav1.domain.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, UUID> {

    @Query(value = "SELECT * FROM student_homework WHERE id_student = :idStudent AND id_homework_announcement = :idHomeworkAnnouncement", nativeQuery = true)
    Optional<StudentHomework> findByIdStudentAndIdHomeworkAnnouncement(@Param("idStudent") UUID idStudent,@Param("idHomeworkAnnouncement") UUID idHomeworkAnnouncement);

    void deleteByHomework(Homework homework);

    @Query(value = "SELECT * FROM student_homework WHERE id_homework_announcement = :idHomeworkAnnouncement", nativeQuery = true)
    List<StudentHomework> findAllByIdHomeworkAnnouncement(@Param("idHomeworkAnnouncement") UUID idHomeworkAnnouncement);

    @Query(value = "SELECT * FROM student_homework WHERE id_homework = :idHomework", nativeQuery = true)
    Optional<StudentHomework> findByIdHomework(@Param("idHomework") UUID idHomework);
}
