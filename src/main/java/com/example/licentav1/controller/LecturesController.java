package com.example.licentav1.controller;

import com.example.licentav1.domain.Lectures;
import com.example.licentav1.dto.LecturesCreationDTO;
import com.example.licentav1.dto.LecturesDTO;
import com.example.licentav1.service.LecturesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/lectures")
public class LecturesController {
    private LecturesService lecturesService;

    public LecturesController(LecturesService lecturesService) {
        this.lecturesService = lecturesService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<LecturesDTO> getLectures() {
        return lecturesService.getLectures();
    }

    @GetMapping("/idCourses={idCourses}")
    @ResponseStatus(HttpStatus.OK)
    public List<LecturesDTO> getLecturesByCourse(@PathVariable("idCourses") UUID idCourses) {
        return lecturesService.getLecturesByCourse(idCourses);
    }

    @GetMapping("/{idLecture}")
    @ResponseStatus(HttpStatus.OK)
    public LecturesDTO getLecture(@PathVariable("idLecture") UUID idLecture){
        return lecturesService.getLecture(idLecture);
    }

    @PostMapping("/create/{idCourse}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createLecture(@RequestBody @Valid LecturesCreationDTO lecturesCreationDTO, @PathVariable("idCourse") UUID idCourse){
        lecturesService.createLecture(lecturesCreationDTO, idCourse);
    }

    @DeleteMapping("/delete/{idLecture}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLecture(@PathVariable("idLecture") UUID idLecture){
        lecturesService.deleteLecture(idLecture);
    }

    @PatchMapping("/update/{idLecture}")
    @ResponseStatus(HttpStatus.OK)
    public void updateLecture(@RequestBody LecturesDTO lecturesDTO, @PathVariable("idLecture") UUID idLecture){
        lecturesService.updateLecture(lecturesDTO, idLecture);
    }
}
