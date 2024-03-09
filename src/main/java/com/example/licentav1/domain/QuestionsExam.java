package com.example.licentav1.domain;

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
@Table(name = "questions_exam")
public class QuestionsExam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_question_exam")
    UUID idQuestionsExam;

    @ManyToOne
    @JoinColumn(name = "question", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "id_exam", nullable = false)
    private Exam exam;
}
