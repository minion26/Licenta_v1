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
}
