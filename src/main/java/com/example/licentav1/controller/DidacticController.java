package com.example.licentav1.controller;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.DidacticRelationNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.dto.DidacticDTO;
import com.example.licentav1.service.DidacticService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/didactic")
public class DidacticController {
    private final DidacticService didacticService;

    public DidacticController(DidacticService didacticService) {
        this.didacticService = didacticService;
    }

    @GetMapping("")
    public List<DidacticDTO> getAllDidactic() {
        return didacticService.getAllDidactic();
    }

    @GetMapping("/{idCourse}")
    @ResponseStatus(HttpStatus.OK)
    public List<DidacticDTO> getDidacticByCourse(@PathVariable UUID idCourse) {
        return didacticService.getDidacticByCourse(idCourse);
    }

    @GetMapping("/idTeacher={idTeacher}")
    @ResponseStatus(HttpStatus.OK)
    public List<DidacticDTO> getDidacticByTeacher(@PathVariable UUID idTeacher) {
        return didacticService.getDidacticByTeacher(idTeacher);
    }


    @PostMapping("/create/course={courseId}&teacher={teacherId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDidactic(@PathVariable UUID courseId, @PathVariable UUID teacherId) {
        didacticService.createDidactic(courseId, teacherId);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        didacticService.uploadFile(file);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    // Teacher Name : first_name + last_name
    public void updateDidactic(@PathVariable UUID id, @RequestBody DidacticDTO didacticDTO) throws DidacticRelationNotFoundException, TeacherNotFoundException, CourseNotFoundException {
        didacticService.updateDidactic(id, didacticDTO);
    }
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDidactic(@PathVariable UUID id) throws DidacticRelationNotFoundException {
        didacticService.deleteDidactic(id);
    }
}
