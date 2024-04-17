package com.example.licentav1.controller;


import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.service.HomeworkService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/homework")
public class HomeworkController {
    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @PostMapping("/upload/idHomeworkAnnouncement={idHomeworkAnnouncement}/idStudent={idStudent}")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadHomework(@RequestParam("file") List<MultipartFile> file, @PathVariable("idHomeworkAnnouncement") UUID idHomeworkAnnouncement,@PathVariable("idStudent") UUID idStudent) throws IOException {
        homeworkService.uploadHomework(file, idHomeworkAnnouncement,idStudent);
    }

    @GetMapping("/download/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return homeworkService.prepareDownloadHomeworkResource(fileName);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<S3ObjectSummary>> listFiles() {
        return new ResponseEntity<>(homeworkService.listFiles(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable UUID id) {
        homeworkService.deleteHomeworkFile(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFile(@RequestParam("file") MultipartFile file, @PathVariable("id") UUID id) throws IOException, InterruptedException {
        homeworkService.updateHomeworkFile(file, id);
    }

}
