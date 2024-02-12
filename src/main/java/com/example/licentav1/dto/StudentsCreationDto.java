package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentsCreationDto {
    private String lastName;
    private String firstName;
    private String facultyEmail;
    private String personalEmail;
    private String nrMatriculation;
    private Integer yearOfStudy;
    private Integer semester;
    private String groupOfStudy;
    private String password;
    private LocalDateTime enrollmentDate;
}
