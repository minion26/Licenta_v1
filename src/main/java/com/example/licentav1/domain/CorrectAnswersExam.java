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
@Table(name = "correct_answers_exam")
public class CorrectAnswersExam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_answer_exam")
    private UUID idQuestion;

    @Column(name = "correct_answer")
    private String correctAnswer;

    @Column(name = "score")
    int score;

    @ManyToOne
    @JoinColumn(name="id_question_exam")
    private QuestionsExam questionsExam;
}
