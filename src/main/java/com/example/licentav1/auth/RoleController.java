package com.example.licentav1.auth;

import com.amazonaws.services.s3.model.JSONOutput;
import com.example.licentav1.dto.UsernameDTO;
import com.example.licentav1.service.UsersService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class RoleController {
    private final UsersService usersService;

    public RoleController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/role")
    public Integer getRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            //fetch the user using the username
            var user = userDetails.getUsername();

            return usersService.getUserByEmail(user).getRoleId();

        }
        return null;
    }

    @GetMapping("/username")
    public UsernameDTO getEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails userDetails) {
            //fetch the user using the username
            UsernameDTO usernameDTO = new UsernameDTO();
            usernameDTO.setUsername(userDetails.getUsername());
//            System.out.println(userDetails.getUsername());
            return usernameDTO;
        }
        return null;
    }
}
