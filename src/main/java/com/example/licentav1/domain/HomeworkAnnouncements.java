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
@Table(name = "homework_announcements")
public class HomeworkAnnouncements {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_homework_announcements")
    private UUID idHomeworkAnnouncements;

    @Column(name="title")
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="due_date")
    private LocalDateTime dueDate;

    @Column(name="score")
    private Integer score;

    @ManyToOne
    @JoinColumn(name = "id_lecture", nullable = false)
    private Lectures lectures;

    @OneToMany(mappedBy = "homeworkAnnouncements")
    private List<Homework> homeworks;
}
