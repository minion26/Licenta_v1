package com.example.licentav1.domain;

import com.amazonaws.services.s3.model.S3ObjectSummary;
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
@Table(name = "materials")
public class Materials {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idMaterial;

    @Column(name="name")
    private String name;

    @Column(name="file_url")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "id_lecture", nullable = false)
    private Lectures lectures;

    @Column(name="material_type")
    private String materialType;


}
