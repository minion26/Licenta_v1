package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "student_exam")
public class StudentExam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_student_exam")
    private UUID idStudentExam;

    // un student poate avea mai multe examene
    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    @JsonBackReference
    private Students student;

    // un examen poate fi sustinut de mai multi studenti
    @ManyToOne
    @JoinColumn(name = "id_exam", nullable = false)
    @JsonBackReference
    private Exam exam;

    @Column(name = "score")
    private int score;
}
