package com.example.licentav1.mapper;

import com.example.licentav1.domain.HomeworkAnnouncements;
import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;
import org.springframework.stereotype.Component;

@Component
public class HomeworkAnnouncementsMapper {
    public static HomeworkAnnouncements fromDTO(HomeworkAnnouncementsCreationDTO homeworkAnnouncementsCreationDTO, Lectures lecture) {
        return HomeworkAnnouncements.builder()
                .title(homeworkAnnouncementsCreationDTO.getTitle())
                .description(homeworkAnnouncementsCreationDTO.getDescription())
                .dueDate(homeworkAnnouncementsCreationDTO.getDueDate())
                .score(homeworkAnnouncementsCreationDTO.getScore())
                .lectures(lecture)
                .build();
    }

    public static HomeworkAnnouncementsDTO toDTO(HomeworkAnnouncements homeworkAnnouncement) {
        return HomeworkAnnouncementsDTO.builder()
                .idHomeworkAnnouncement(homeworkAnnouncement.getIdHomeworkAnnouncements())
                .title(homeworkAnnouncement.getTitle())
                .description(homeworkAnnouncement.getDescription())
                .dueDate(homeworkAnnouncement.getDueDate())
                .score(homeworkAnnouncement.getScore())
                .idLectures(homeworkAnnouncement.getLectures().getIdLecture())
                .build();
    }
}
