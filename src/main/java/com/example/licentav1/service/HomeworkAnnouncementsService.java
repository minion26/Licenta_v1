package com.example.licentav1.service;

import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;

import java.util.List;
import java.util.UUID;

public interface HomeworkAnnouncementsService {
    void createHomeworkAnnouncement(UUID idLecture, HomeworkAnnouncementsCreationDTO homeworkAnnouncementsCreationDTO);

    List<HomeworkAnnouncementsDTO> getHomeworkAnnouncements(UUID idLecture);

    void deleteHomeworkAnnouncement(UUID idHomeworkAnnouncement);
}
