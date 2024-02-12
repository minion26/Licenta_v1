package com.example.licentav1.controller;

import com.example.licentav1.domain.Roles;
import com.example.licentav1.domain.Users;
import com.example.licentav1.service.RolesService;
import com.example.licentav1.service.UsersService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UsersController {
    private final UsersService usersService;
    private final RolesService rolesService;

    public UsersController(UsersService usersService, RolesService rolesService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
    }

    @GetMapping("/users")
    public Iterable<Users> getUsers(){
        return usersService.getUsers();
    }

    @PostMapping("/create/users")
    public void createUsers(@RequestBody Users users){
        usersService.createUsers(users);
    }

    @GetMapping("/roles/{id}")
    public Optional<Roles> getRoleById(@PathVariable(value="id") int id) {
        return rolesService.getRoleById(id);
    }
}
