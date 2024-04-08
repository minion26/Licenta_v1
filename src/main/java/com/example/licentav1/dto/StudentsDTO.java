package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentsDTO {
    private UUID idUsers;
    private String firstName;
    private String lastName;
    private String facultyEmail;
    private String personalEmail;
    private String nrMatriculation;
    private Integer yearOfStudy;
    private Integer semester;
    private String groupOfStudy;
    private LocalDateTime enrollmentDate;
}
