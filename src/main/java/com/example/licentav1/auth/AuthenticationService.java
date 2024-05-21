package com.example.licentav1.auth;

import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.Users;
import com.example.licentav1.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository usersRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getFacultyEmail(),
                        request.getPassword()
                )
        );
        //generate token and sent it back
        var user = usersRepository.findByFacultyEmail(request.getFacultyEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Users.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .facultyEmail(request.getFacultyEmail())
                .personalEmail(request.getPersonalEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roleId(1)
                .build();
        usersRepository.save(user);
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
