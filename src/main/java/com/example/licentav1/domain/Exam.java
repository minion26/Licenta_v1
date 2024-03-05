package com.example.licentav1.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_exam")
    UUID idExam;

    @Column(name="name")
    String name;

    @Column(name="questions")
    String questions;

    @Column(name="time_in_minutes")
    Integer timeInMinutes;

    @Column(name="total_score")
    Integer totalScore;

    @Column(name="passing_score")
    Integer passingScore;

    @Column(name="date")
    LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="course_id")
    Courses course;

    @ManyToMany(mappedBy = "exams")
    List<Teachers> teachers;
}
