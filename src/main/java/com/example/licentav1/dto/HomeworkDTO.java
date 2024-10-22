package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeworkDTO {
    UUID idHomework;
    UUID idStudent;
    String nrMatricol;
    String firstNameStudent;
    String lastNameStudent;
    Integer grade;
    LocalDateTime uploadDate;
    List<String> fileName;

}
