package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Roles;
import com.example.licentav1.domain.Users;
import com.example.licentav1.repository.RolesRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.UsersService;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;


    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }


    @Override
    public Iterable<Users> getUsers() {
        return usersRepository.findAll();
    }

    @Override
    public void createUsers(Users users) {
        usersRepository.save(users);

    }
}
