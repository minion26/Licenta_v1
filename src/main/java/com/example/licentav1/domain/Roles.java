package com.example.licentav1.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_roles")
    private int idRoles;

    @Column(name = "role_name")
    private String roleName;
}
