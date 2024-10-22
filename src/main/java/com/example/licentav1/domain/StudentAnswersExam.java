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
@Table(name = "student_answers_exam",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_student_exam", "id_question_exam"}))
public class StudentAnswersExam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_student_answer_exam")
    private UUID idStudentAnswerExam;

    @Column(name = "student_answer")
    private String studentAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student_exam", nullable = false)
    private StudentExam studentExam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_question_exam", nullable = false)
    private QuestionsExam questionsExam;

    @Column(name="needs_review")
    private boolean needsReview;
}
