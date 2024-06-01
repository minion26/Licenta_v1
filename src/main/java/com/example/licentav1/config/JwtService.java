package com.example.licentav1.config;

import com.example.licentav1.domain.Users;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;

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

    public UUID extractUserId(Users user) {
        return user.getIdUsers();
    }

    public String generateToken(UserDetails userDetails, HttpServletResponse response){
        return generateToken(new HashMap<>(), userDetails, response);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails,  HttpServletResponse response) {
        //delete old cookie
        ResponseCookie oldCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0) // Set max age to 0 to delete the cookie
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, oldCookie.toString());

        Users users = usersRepository.findByFacultyEmail(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        UUID userId = users.getIdUsers();
        extraClaims.put("userId", userId);

        Integer roleId = users.getRoleId();
        extraClaims.put("roleId", roleId);
        System.out.println("put role id: " + roleId);

        if (roleId == 3){
            //STUDENT
            var student = studentsRepository.findById(userId).orElseThrow(() -> new RuntimeException("Student not found"));
            Integer yearOfStudy = student.getYearOfStudy();
            extraClaims.put("yearOfStudy", yearOfStudy);

            String group = student.getGroupOfStudy();
            extraClaims.put("group", group);

        }

//        long nowMillis = System.currentTimeMillis();
//        long expMillis = nowMillis + TimeUnit.DAYS.toMillis(1);
//        Date exp = new Date(expMillis);

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime exp = now.plusDays(1);

        System.out.println("Token expires at: " + exp);

        var jwtToken = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(now.toInstant())) //to calculate if the token is expired
                .setExpiration(Date.from(exp.toInstant())) //1 day
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        long maxAge = Duration.between(now, exp).getSeconds();

        ResponseCookie cookie = ResponseCookie.from("accessToken", jwtToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge) // 1 day
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());



        return jwtToken;
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

    public String extractRole(String token) {
        System.out.println("Extracting role from token"+ token);
        Claims claims = extractAllClaims(token);
        Integer roleId = claims.get("roleId", Integer.class);
        System.out.println("Role id: " + roleId);

        // Convert roleId to role name
        String role  = switch (roleId) {
            case 1 -> "ADMIN";
            case 2 -> "TEACHER";
            case 3 -> "STUDENT";
            default -> "UNKNOWN";
        };

        System.out.println("Role: " + role);

        return role;
    }
}
