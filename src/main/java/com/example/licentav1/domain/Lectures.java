package com.example.licentav1.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "lectures")
public class Lectures {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_lecture")
    UUID idLecture;

    @Column(name="name")
    String name;

    @Column(name="description")
    String description;

    @Column(name="week")
    Integer week;

    @Column(name="semester")
    Integer semester;

    @Column(name="year")
    Integer year;

    @ManyToOne
    @JoinColumn(name = "id_course", nullable = false)
    private Courses courses;

    @OneToMany(mappedBy="lectures")
    private List<Materials> materials;
}
