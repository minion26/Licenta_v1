package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachersDTO {
    private String firstName;
    private String lastName;
    private String facultyEmail;
    private String personalEmail;
    private String idTeacher;
    private String degree;
}
