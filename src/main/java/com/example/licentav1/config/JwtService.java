package com.example.licentav1.config;

import com.example.licentav1.domain.Users;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private final UsersRepository usersRepository;

    private final StudentsRepository studentsRepository;


    private static final String SECRET_KEY = "6424ca777123aa812b726c5051585d747941a44d2e7787b145cee7c6b97d826f";

    public JwtService(UsersRepository usersRepository, StudentsRepository studentsRepository) {
        this.usersRepository = usersRepository;
        this.studentsRepository = studentsRepository;
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject) ; //for the moment
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Users users = usersRepository.findByFacultyEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        UUID userId = users.getIdUsers();
        extraClaims.put("userId", userId);

        Integer roleId = users.getRoleId();
        extraClaims.put("roleId", roleId);

        if (roleId == 3){
            //STUDENT
            var student = studentsRepository.findById(userId).orElseThrow(() -> new RuntimeException("Student not found"));
            Integer yearOfStudy = student.getYearOfStudy();
            extraClaims.put("yearOfStudy", yearOfStudy);

            String group = student.getGroupOfStudy();
            extraClaims.put("group", group);

        }

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) //to calculate if the token is expired
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //1 day
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        // i want to check if this token belongs to this user
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //when we need to extract other information from the token
    //we need to use the signature key
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
