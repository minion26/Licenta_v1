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
@Table(name = "students_follow_courses")
public class StudentsFollowCourses {
    @Id
    @Column(name = "id_students_follow_course")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idStudentsFollowCourses;

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    private Students student;

    @ManyToOne
    @JoinColumn(name = "id_course", nullable = false)
    private Courses course;

    public StudentsFollowCourses(Students student, Courses course) {
        this.student = student;
        this.course = course;
    }
}
