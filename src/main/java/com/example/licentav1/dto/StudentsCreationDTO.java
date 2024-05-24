package com.example.licentav1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "First name is required")
    @Size(min=1, max=50, message = "First name must be between 1 and 50 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min=1, max=50, message = "Last name must be between 1 and 50 characters")
    private String lastName;

    @NotNull(message = "Faculty email is required")
    @Size(min=1, max=50, message = "Faculty email must be between 1 and 50 characters")
    private String facultyEmail;

    @NotNull(message = "Personal email is required")
    @Size(min=1, max=50, message = "Personal email must be between 1 and 50 characters")
    private String personalEmail;

//    @NotNull(message = "Password is required")
//    @Size(min=1, max=50, message = "Password must be between 1 and 50 characters")
//    private String password;

    @NotNull(message = "Nr matriculation is required")
    @Size(min=1, max=50, message = "Nr matriculation must be between 1 and 50 characters")
    private String nrMatriculation;

    @NotNull(message = "Year of study is required")
    @Min(value = 1, message = "Year of study must be at least 1")
    @Max(value = 3, message = "Year of study must be at most 3")
    private Integer yearOfStudy;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 2, message = "Semester must be at most 2")
    private Integer semester;

    @NotNull(message = "Group of study is required")
    @Size(min=1, max=50, message = "Group of study must be between 1 and 50 characters")
    private String groupOfStudy;

//    @NotNull(message = "Enrollment date is required")
//    private LocalDateTime enrollmentDate;
}
