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
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_feedback")
    private UUID idFeedback;

    @Column(name="content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "id_homework", nullable = false)
    @JsonBackReference
    private Homework homework;


    @Column(name= "positionX")
    private Integer positionX;

    @Column(name= "positionY")
    private Integer positionY;

    @Column(name= "notetext")
    private String noteText;

}
