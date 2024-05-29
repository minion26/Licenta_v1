package com.example.licentav1.auth;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LogoutController {


    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.getContext().setAuthentication(null);
        Cookie jwtCookie = new Cookie("accessToken", null);
        jwtCookie.setMaxAge(0); //the cookie will expire immediately
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/"); // Make sure this matches the path of the cookie you're trying to clear
        response.addCookie(jwtCookie);
        return "You have been logged out";
    }
}
