package com.example.licentav1.repository;

import com.example.licentav1.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RolesRepository extends JpaRepository<Roles, Integer> {

    @Query(value = "SELECT * FROM roles WHERE role_name = :roleName", nativeQuery = true)
    Roles findByRoleName(@Param("roleName") String roleName);
}
