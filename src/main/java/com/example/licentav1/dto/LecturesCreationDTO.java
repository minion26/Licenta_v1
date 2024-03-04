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

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturesCreationDTO {
    @NotNull(message = "Name is required")
    @Size(min=1, max=100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;

    @NotNull(message = "Week is required")
    @Min(value = 1, message = "Week must be at least 1")
    @Max(value = 14, message = "Week must be at most 14")
    private Integer week;

}
