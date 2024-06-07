package com.example.licentav1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentHomeworkDTO {
    private UUID idStudentHomework;
    private UUID idHomework;
    private UUID idStudent; //il am deja din get
    private List<UUID> idHomeworkFiles = new ArrayList<>();
    private String homeworkName;
    private Integer grade; //asta il iau din homework cu ajutorul lui idHomework

}
