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
public class LecturesDTO {
    private UUID idLecture;
    private String name;
    private String description;
    private Integer week;
    private Integer semester;
    private Integer year;

}
