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
@Table(name = "students")
public class Students{

    @Id
    @Column(name = "id_users")
    private UUID idUsers;

    @Column(name = "nr_matriculation")
    private String nrMatriculation;

    @Column(name = "year_of_study")
    private Integer yearOfStudy;

    @Column(name = "group_of_study")
    private String groupOfStudy;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    //The mappedBy attribute should match the name of the field in
    // the StudentsFollowCourses entity that references
    // the Students entity.
    @OneToMany(mappedBy = "student")
    private List<StudentsFollowCourses> courses;
}
