package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "student_homework")
public class StudentHomework {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_student_homework")
    private UUID idStudentHomework;

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    @JsonBackReference
    private Students student;

    @ManyToOne
    @JoinColumn(name = "id_homework", nullable = false)
    private Homework homework;
}
