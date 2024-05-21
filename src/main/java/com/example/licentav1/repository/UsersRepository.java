package com.example.licentav1.repository;

import com.example.licentav1.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//DAO
@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {

    @Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE u.faculty_email = :facultyEmail", nativeQuery = true)
    boolean existsByFacultyEmail(@Param("facultyEmail") String facultyEmail);

    @Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE u.personal_email = :personalEmail", nativeQuery = true)
    boolean existsByPersonalEmail(@Param("personalEmail") String personalEmail);

    @Query(value = "SELECT * FROM Users u WHERE u.faculty_email = :facultyEmail", nativeQuery = true)
    Optional<Users> findByFacultyEmail(@Param("facultyEmail") String facultyEmail);
}
