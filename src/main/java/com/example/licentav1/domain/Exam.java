package com.example.licentav1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_exam")
    private UUID idExam;

    @Column(name="name")
    private String name;


    @Column(name="time_in_minutes")
    private Integer timeInMinutes;

    @Column(name="total_score")
    private Integer totalScore;

    @Column(name="passing_score")
    private Integer passingScore;

    @Column(name="date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name="course_id")
    private Courses course;

    @ManyToMany
    @JoinTable(
            name = "teacher_exam",
            joinColumns = @JoinColumn(name = "id_exam"),
            inverseJoinColumns = @JoinColumn(name = "id_teacher")
    )
    @JsonBackReference
    private List<Teachers> teachers;

    @OneToMany(mappedBy = "exam")
    @JsonManagedReference
    private List<Question> questionsList;

    @OneToMany(mappedBy = "exam")
    @JsonManagedReference
    private List<StudentExam> studentExams;

    @OneToMany(mappedBy = "exam")
    @JsonManagedReference
    private List<QuestionsExam> questionsExams;

    @Column(name="has_started")
    private Boolean hasStarted;
}
