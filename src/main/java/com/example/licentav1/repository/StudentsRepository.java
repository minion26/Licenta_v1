package com.example.licentav1.repository;

import com.example.licentav1.domain.Students;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface StudentsRepository extends CrudRepository<Students, UUID> {

}
