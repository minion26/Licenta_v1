package com.example.licentav1.service;

import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UsersDTO;

public interface UsersService {

    Iterable<Users> getUsers();

    void createUsers(UsersDTO usersDTO);

}
