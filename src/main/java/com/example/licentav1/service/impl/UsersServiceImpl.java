package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.LastAdminException;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.Roles;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UserChangePasswordDTO;
import com.example.licentav1.dto.UserEditDTO;
import com.example.licentav1.dto.UsersDTO;
import com.example.licentav1.mapper.UsersMapper;
import com.example.licentav1.repository.RolesRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Struct;
import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, JwtService jwtService, HttpServletRequest request, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.request = request;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public Iterable<UsersDTO> getUsers() {
        return usersRepository.findAll().stream().map(UsersMapper::toDto).toList();
    }

    @Override
    public Iterable<UsersDTO> getAdmins() {
        return usersRepository.findAll().stream().filter(u -> u.getRoleId() == 1).map(UsersMapper::toDto).toList();
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID idToken = jwtService.getUserIdFromToken(token);
        System.out.println("id from token: " + idToken);

        Users user = usersRepository.findById(idToken).orElseThrow(() -> new UserNotFoundException("User not found"));

        System.out.println("Old password: " + userChangePasswordDTO.getOldPassword());
        System.out.println("New password: " + userChangePasswordDTO.getNewPassword());
        System.out.println("Confirm password: " + userChangePasswordDTO.getConfirmPassword());

        if (!passwordEncoder.matches(userChangePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new NonAllowedException("Old password is incorrect");
        }

        if (!userChangePasswordDTO.getNewPassword().equals(userChangePasswordDTO.getConfirmPassword())) {
            throw new NonAllowedException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(userChangePasswordDTO.getNewPassword()));
        usersRepository.save(user);
    }

    @Override
    public void createUsers(UsersDTO usersDTO) throws UserAlreadyExistsException {

        if (usersRepository.existsByFacultyEmail(usersDTO.getFacultyEmail())) {
            throw new UserAlreadyExistsException("User's faculty email already exists");
        }
        if (usersRepository.existsByPersonalEmail(usersDTO.getPersonalEmail())) {
            throw new UserAlreadyExistsException("User's personal email already exists");
        }

        Users users = UsersMapper.fromDto(usersDTO);
        String password = UUID.randomUUID().toString().substring(0, 8);
        System.out.println(password);

        users.setPassword(passwordEncoder.encode(password));
        usersRepository.save(users);

    }

    @Override
    public void updateUsers(UUID id, UserEditDTO usersDTO) throws UserNotFoundException {
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (usersDTO.getFirstName() != null) {
            users.setFirstName(usersDTO.getFirstName());
        }
        if (usersDTO.getLastName() != null) {
            users.setLastName(usersDTO.getLastName());
        }
        if (usersDTO.getFacultyEmail() != null) {
            users.setFacultyEmail(usersDTO.getFacultyEmail());
        }
//        if(usersDTO.getRoleId() != null) {
//            users.setRoleId(usersDTO.getRoleId());
//        }
        if (usersDTO.getPersonalEmail() != null) {
            users.setPersonalEmail(usersDTO.getPersonalEmail());
        }
//        if (usersDTO.getPassword() != null) {
//            users.setPassword(passwordEncoder.encode(usersDTO.getPassword()));
//        }
//        if (usersDTO.getRoleId() != null) {
//            users.setRoleId(usersDTO.getRoleId());
//        }
        usersRepository.save(users);
    }

    @Override
    public void deleteUsers(UUID id) throws LastAdminException, UserNotFoundException {
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (users.getRoleId() == 1) {
            if (usersRepository.findAll().stream().filter(u -> u.getRoleId() == 1).count() == 1) {
                throw new LastAdminException("Cannot delete last admin");
            }
        }

        usersRepository.delete(users);

    }

    @Override
    public UsersDTO getUserByEmail(String email) {
        Users users = usersRepository.findByFacultyEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        return UsersMapper.toDto(users);
    }


}
