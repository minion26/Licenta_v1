package com.example.licentav1.controller;

import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.service.LecturesService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/lectures")
public class LecturesController {
    private LecturesService lecturesService;

    public LecturesController(LecturesService lecturesService) {
        this.lecturesService = lecturesService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<Lectures> getLectures() {
        return lecturesService.getLectures();
    }

    @PostMapping("/create/{idCourse}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLecture(@RequestBody LecturesCreationDTO lecturesCreationDTO, @PathVariable("idCourse") UUID idCourse){
        lecturesService.createLecture(lecturesCreationDTO, idCourse);
    }


}
