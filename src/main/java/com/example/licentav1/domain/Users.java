package com.example.licentav1.domain;

import jakarta.persistence.*;
import lombok.*;

import javax.net.ssl.SSLSession;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id_users")
    private UUID idUsers;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "faculty_email")
    private String facultyEmail;

    @Column(name = "personal_email")
    private String personalEmail;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    private Integer roleId;

}
