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


@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursesCreationDTO {
    @NotNull(message = "Name is required")
    @Size(min=1, max=100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be between at least 1")
    @Max(value = 2, message = "Semester must be between at most 2")
    private Integer semester;

    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be between at least 1")
    private Integer credits;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;
}
