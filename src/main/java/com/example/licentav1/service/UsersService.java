package com.example.licentav1.service;

import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UserChangePasswordDTO;
import com.example.licentav1.dto.UserEditDTO;
import com.example.licentav1.dto.UsersDTO;

import java.util.UUID;

public interface UsersService {

    Iterable<UsersDTO> getUsers();

    void createUsers(UsersDTO usersDTO);

    void updateUsers(UUID id, UserEditDTO usersDTO);

    void deleteUsers(UUID id);

    UsersDTO getUserByEmail(String email);

    Iterable<UsersDTO> getAdmins();

    void changePassword(UserChangePasswordDTO userChangePasswordDTO);

    UsersDTO getTheSuperuser();

    void makeSuperuser(UUID idUser);
}
