package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.LastAdminException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Roles;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UsersDTO;
import com.example.licentav1.service.RolesService;
import com.example.licentav1.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class UsersController {
    private final UsersService usersService;
    private final RolesService rolesService;

    public UsersController(UsersService usersService, RolesService rolesService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<UsersDTO> getUsers(){
        return usersService.getUsers();
    }
    @GetMapping("/roles/{id}")
    public Optional<Roles> getRoleById(@PathVariable(value="id") int id) {
        return rolesService.getRoleById(id);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUsers(@RequestBody UsersDTO usersDTO) throws UserAlreadyExistsException {
        usersService.createUsers(usersDTO);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUsers(@PathVariable(value="id") UUID id , @RequestBody UsersDTO usersDTO) throws UserNotFoundException{
        usersService.updateUsers(id, usersDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsers(@PathVariable(value="id") UUID id) throws LastAdminException, UserNotFoundException {
        usersService.deleteUsers(id);
    }
}
