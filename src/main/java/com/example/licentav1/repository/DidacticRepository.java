package com.example.licentav1.repository;

import com.example.licentav1.domain.Didactic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DidacticRepository extends JpaRepository<Didactic, UUID> {
}