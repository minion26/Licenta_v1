package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.LastAdminException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Roles;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.UserChangePasswordDTO;
import com.example.licentav1.dto.UserEditDTO;
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

    @GetMapping("/email/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UsersDTO getUserByEmail(@PathVariable(value="email") String email) throws UserNotFoundException {
        return usersService.getUserByEmail(email);
    }

    @GetMapping("/admins")
    @ResponseStatus(HttpStatus.OK)
    public Iterable<UsersDTO> getAdmins() {
        return usersService.getAdmins();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUsers(@RequestBody UsersDTO usersDTO) throws UserAlreadyExistsException {
        usersService.createUsers(usersDTO);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUsers(@PathVariable(value="id") UUID id , @RequestBody UserEditDTO usersDTO) throws UserNotFoundException{
        usersService.updateUsers(id, usersDTO);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUsers(@PathVariable(value="id") UUID id) throws LastAdminException, UserNotFoundException {
        usersService.deleteUsers(id);
    }

    @PostMapping("/changePassword")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@RequestBody UserChangePasswordDTO userChangePasswordDTO) throws UserNotFoundException {
        usersService.changePassword(userChangePasswordDTO);
    }

    @GetMapping("/get-the-superuser")
    @ResponseStatus(HttpStatus.OK)
    public UsersDTO getTheSuperuser() {
        return usersService.getTheSuperuser();
    }

    @PostMapping("/make-superuser/{idUser}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void makeSuperuser(@PathVariable(value="idUser") UUID idUser) {
        usersService.makeSuperuser(idUser);
    }
}
