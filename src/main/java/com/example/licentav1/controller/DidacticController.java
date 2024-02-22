package com.example.licentav1.controller;

import com.example.licentav1.service.DidacticService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/didactic")
public class DidacticController {
    private final DidacticService didacticService;

    public DidacticController(DidacticService didacticService) {
        this.didacticService = didacticService;
    }



    @PostMapping("/create/course={courseId}&teacher={teacherId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDidactic(@PathVariable UUID courseId, @PathVariable UUID teacherId) {
        didacticService.createDidactic(courseId, teacherId);
    }

}
