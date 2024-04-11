package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_homework")
    private UUID idHomework;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="grade")
    private Integer grade;

    @Column(name="duedate")
    private LocalDateTime dueDate;

    @Column(name="file_url")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "id_lecture", nullable = false)
    @JsonBackReference
    private Lectures lectures;

    @OneToMany(mappedBy = "homework")
    @JsonManagedReference
    private List<Feedback> feedbacks;
}
