package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEditDTO {
    private UUID idUsers;
    private String firstName;
    private String lastName;
    private String facultyEmail;
    private String personalEmail;
}
