package com.example.licentav1.mapper;

import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.HomeworkAnnouncements;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Component
public class HomeworkMapper {

    public static Homework map(HomeworkAnnouncements homeworkAnnouncements) {
        Homework homework = new Homework();
        homework.setHomeworkAnnouncements(homeworkAnnouncements);
        homework.setGrade(-1);
        LocalDateTime now = LocalDateTime.now();
        homework.setDueDate(now);
        return homework;
    }
}
