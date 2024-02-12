package com.example.licentav1.repository;

import com.example.licentav1.domain.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

//DAO
@Repository
public interface UsersRepository extends CrudRepository<Users, Integer> {


}
