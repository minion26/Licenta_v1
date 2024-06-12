package com.example.licentav1.repository;

import com.example.licentav1.domain.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface StudentsRepository extends JpaRepository<Students, UUID> {
    @Query(value = "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Students s WHERE s.nr_matriculation = :nrMatriculation", nativeQuery = true)
    boolean existsByNrMatriculation(@Param("nrMatriculation") String nrMatriculation);

    @Query(value = "SELECT * FROM students WHERE nr_matriculation = :nrMatriculation", nativeQuery = true)
    Optional<Students> findByNrMatriculation(@Param("nrMatriculation") String nrMatriculation);

    @Query(value = "SELECT s.id_users FROM Students s JOIN Users u ON s.id_users = u.id_users WHERE u.first_name = :firstName AND u.last_name = :lastName", nativeQuery = true)
    UUID findByName(@Param("firstName") String firstName,@Param("lastName") String lastName);
}
