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
public class StudentsCreationDTO {
    private String firstName;
    private String lastName;
    private String facultyEmail;
    private String personalEmail;
    private String password;
    private String nrMatriculation;
    private Integer yearOfStudy;
    private Integer semester;
    private String groupOfStudy;
    private LocalDateTime enrollmentDate;
}
