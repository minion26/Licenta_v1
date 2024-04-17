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

    @Column(name="grade")
    private Integer grade;

    @Column(name="duedate")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "id_homework_announcement", nullable = false)
    private HomeworkAnnouncements homeworkAnnouncements;

    @OneToMany(mappedBy = "homework")
    @JsonManagedReference
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "homework")
    @JsonManagedReference
    private List<HomeworkFiles> homeworkFiles;
}
