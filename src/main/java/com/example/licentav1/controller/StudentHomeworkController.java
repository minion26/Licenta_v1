package com.example.licentav1.controller;

import com.example.licentav1.dto.StudentHomeworkDTO;
import com.example.licentav1.service.StudentHomeworkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/student-homework")
public class StudentHomeworkController {
    private final StudentHomeworkService studentHomeworkService;

    public StudentHomeworkController(StudentHomeworkService studentHomeworkService) {
        this.studentHomeworkService = studentHomeworkService;
    }

    @GetMapping("/check-post/idHomeworkAnnouncement={idHomeworkAnnouncement}/idStudent={idStudent}")
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkPost(@PathVariable("idHomeworkAnnouncement") UUID idHomeworkAnnouncement, @PathVariable("idStudent") UUID idStudent) {
        return studentHomeworkService.checkPost(idHomeworkAnnouncement, idStudent);
    }

    //endpoint doar pentru studenti
    @GetMapping("/all-by-student")
    @ResponseStatus(HttpStatus.OK)
    public List<StudentHomeworkDTO> getAll() {
        return studentHomeworkService.getAllByStudent();
    }

    @GetMapping("/get-idHomework/idHomeworkAnnouncement={idHomeworkAnnouncement}")
    @ResponseStatus(HttpStatus.OK)
    public UUID getIdHomework(@PathVariable("idHomeworkAnnouncement") UUID idHomeworkAnnouncement) {
        return studentHomeworkService.getIdHomework(idHomeworkAnnouncement);
    }

}
