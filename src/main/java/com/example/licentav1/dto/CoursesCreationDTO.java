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
public class CoursesCreationDTO {
    private String name;
    private Integer year;
    private Integer semester;
    private Integer credits;
    private String description;
}