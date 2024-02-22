package com.example.licentav1.domain;

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
@Table(name = "didactic")
public class Didactic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_didactic")
    private UUID idDidactic;


    @ManyToOne
    @JoinColumn(name = "id_courses", nullable = false)
    private Courses courses;

    @ManyToOne
    @JoinColumn(name = "id_teacher", nullable = false)
    private Teachers teachers;

    public Didactic(Teachers teachers, Courses courses) {
        this.teachers = teachers;
        this.courses = courses;
    }
}
