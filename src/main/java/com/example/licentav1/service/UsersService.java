package com.example.licentav1.service;

import com.example.licentav1.domain.Users;

public interface UsersService {

    Iterable<Users> getUsers();

    void createUsers(Users users);

}
