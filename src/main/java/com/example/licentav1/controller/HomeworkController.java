package com.example.licentav1.controller;

import com.example.licentav1.service.HomeworkService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/homework")
public class HomeworkController {
    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }



}
