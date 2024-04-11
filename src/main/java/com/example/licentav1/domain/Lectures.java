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
@Table(name = "lectures")
public class Lectures {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_lecture")
    private UUID idLecture;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    @Column(name="week")
    private Integer week;

    @Column(name="semester")
    private Integer semester;

    @Column(name="year")
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "id_course", nullable = false)
    private Courses courses;

    @OneToMany(mappedBy="lectures")
    private List<Materials> materials;

    // mappedBy = "lectures" indică faptul că relația este gestionată de câmpul lecture din clasa Homework.
    @OneToMany(mappedBy = "lectures")
    @JsonManagedReference
    private List<Homework> homeworks;
}
