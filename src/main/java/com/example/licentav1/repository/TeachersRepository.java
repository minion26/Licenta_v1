package com.example.licentav1.repository;

import com.example.licentav1.domain.Teachers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TeachersRepository extends JpaRepository<Teachers, UUID> {
    @Query(value = "SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Teachers t WHERE t.id_teacher = :idTeacher", nativeQuery = true)
    boolean existsByIdTeacher(@Param("idTeacher") String idTeacher);

    @Query(value = "SELECT * FROM Teachers t WHERE t.id_teacher = :teacherId", nativeQuery = true)
    Optional<Teachers> findByTeacherId(@Param("teacherId") String teacherId);

    @Query(value = "SELECT t.id_users FROM Teachers t JOIN Users u ON t.id_users = u.id_users WHERE u.first_name = :firstName AND u.last_name = :lastName", nativeQuery = true)
    UUID findByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
}
