package com.example.licentav1.mapper;

import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.HomeworkAnnouncements;
import com.example.licentav1.domain.StudentHomework;
import com.example.licentav1.domain.Students;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StudentHomeworkMapper {

    public static StudentHomework map(HomeworkAnnouncements homeworkAnnouncements, Students student, Homework homework) {
        StudentHomework newStudentHomework = new StudentHomework();
        newStudentHomework.setStudent(student);
        newStudentHomework.setHomework(homework);
        newStudentHomework.setHomeworkAnnouncements(homeworkAnnouncements);
        return newStudentHomework;
    }
}
