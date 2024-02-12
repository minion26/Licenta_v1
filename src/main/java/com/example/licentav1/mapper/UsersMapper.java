package com.example.licentav1.mapper;

import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UsersDto;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {
    public UsersDto toDto(Users users) {

        return UsersDto.builder()
                .idUsers(users.getIdUsers())
                .password(users.getPassword())
                .facultyEmail(users.getFacultyEmail())
                .personalEmail(users.getPersonalEmail())
                .roleId(users.getRoleId())
                .build();
    }

    public Users fromDto(UsersDto usersDto) {
        return Users.builder()
                .idUsers(usersDto.getIdUsers())
                .password(usersDto.getPassword())
                .facultyEmail(usersDto.getFacultyEmail())
                .personalEmail(usersDto.getPersonalEmail())
                .roleId(usersDto.getRoleId())
                .build();
    }
}
