package com.example.licentav1.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TeachersCreationDTO {
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

    @NotNull(message = "Id teacher is required")
    @Size(min=1, max=50, message = "Id teacher must be between 1 and 50 characters")
    private String idTeacher;

    @NotNull(message = "Degree is required")
    private String degree;
}
