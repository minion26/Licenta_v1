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
@Table(name = "courses")
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_courses")
    private UUID idCourses;

    @Column(name = "name")
    private String name;

    @Column(name= "year")
    private Integer year;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "courses")
    private List<Didactic> didactics;

    @OneToMany(mappedBy = "course")
    private List<StudentsFollowCourses> students;

    @OneToMany(mappedBy = "courses")
    private List<Lectures> lectures;

    @OneToMany(mappedBy = "course")
    List<Exam> exams;

    @ManyToMany
    @JoinTable(
            name = "didactic",
            joinColumns = @JoinColumn(name = "id_courses"),
            inverseJoinColumns = @JoinColumn(name = "id_teacher")
    )
    private List<Teachers> teachers;
}
