package com.example.licentav1.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
}
