package com.example.licentav1.repository;

import com.example.licentav1.domain.Materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, UUID> {
}
