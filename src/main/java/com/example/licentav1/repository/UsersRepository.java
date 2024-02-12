package com.example.licentav1.repository;

import com.example.licentav1.domain.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
//DAO
@Repository
public interface UsersRepository extends CrudRepository<Users, Integer> {

}
