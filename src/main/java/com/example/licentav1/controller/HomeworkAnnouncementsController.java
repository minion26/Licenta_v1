package com.example.licentav1.controller;

import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;
import com.example.licentav1.service.HomeworkAnnouncementsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/homework-announcements")
public class HomeworkAnnouncementsController {
    private final HomeworkAnnouncementsService homeworkAnnouncementService;

    public HomeworkAnnouncementsController(HomeworkAnnouncementsService homeworkAnnouncementService) {
        this.homeworkAnnouncementService = homeworkAnnouncementService;
    }

    @PostMapping("/create/idLecture={idLecture}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHomeworkAnnouncement(@PathVariable UUID idLecture, @RequestBody HomeworkAnnouncementsCreationDTO homeworkAnnouncementsCreationDTO) {
        homeworkAnnouncementService.createHomeworkAnnouncement(idLecture, homeworkAnnouncementsCreationDTO);
    }

    @GetMapping("/idLecture={idLecture}")
    @ResponseStatus(HttpStatus.OK)
    public List<HomeworkAnnouncementsDTO> getHomeworkAnnouncements(@PathVariable UUID idLecture) {
        return homeworkAnnouncementService.getHomeworkAnnouncements(idLecture);
    }

    @DeleteMapping("/delete/idHomeworkAnnouncement={idHomeworkAnnouncement}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteHomeworkAnnouncement(@PathVariable UUID idHomeworkAnnouncement) {
        homeworkAnnouncementService.deleteHomeworkAnnouncement(idHomeworkAnnouncement);
    }


}
