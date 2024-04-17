package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "homework_files")
public class HomeworkFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_homework_files")
    private UUID idHomeworkFiles;

    @ManyToOne
    @JoinColumn(name = "id_homework", nullable = false)
    @JsonBackReference
    private Homework homework;

    @Column(name = "file_url")
    private String fileUrl;
}
