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
@Table(name = "teacher_exam")
public class TeacherExam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_teacher_exam")
    private UUID idTeacherExam;

    @ManyToOne
    @JoinColumn(name = "id_teacher", nullable = false)
    @JsonBackReference
    private Teachers teacher;

    @ManyToOne
    @JoinColumn(name = "id_exam", nullable = false)
    @JsonBackReference
    private Exam exam;
}
