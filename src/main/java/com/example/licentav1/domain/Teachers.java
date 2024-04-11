package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "teachers")
public class Teachers {
    @Id
    @Column(name = "id_users")
    private UUID idUsers;

    @Column(name = "id_teacher")
    private String idTeacher;

    @Column(name = "degree")
    private String degree;

    @OneToMany(mappedBy = "teachers")
    private List<Didactic> didactics;

    @ManyToMany
    @JoinTable(
            name = "teacher_exam",
            joinColumns = @JoinColumn(name = "id_teacher"),
            inverseJoinColumns = @JoinColumn(name = "id_exam")
    )
    @JsonManagedReference
    private List<Exam> exams;

    @ManyToMany(mappedBy = "teachers")
    private List<Courses> courses;
}
