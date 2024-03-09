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
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_question")
    private UUID idQuestion;

    @Column(name = "question_text")
    private String questionText;

    @ManyToOne
    @JoinColumn(name="id_exam")
    @JsonBackReference
    private Exam exam;
}
