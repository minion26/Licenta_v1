package com.example.licentav1.controller;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.service.MaterialsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/materials")
public class MaterialsController {

    private MaterialsService materialsService;

    private static final Logger logger = LoggerFactory.getLogger(MaterialsController.class);

    public MaterialsController(MaterialsService materialsService) {
        this.materialsService = materialsService;
    }

    @PostMapping(path="/upload/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") MultipartFile file, @PathVariable("id") UUID id ) throws IOException {
        materialsService.uploadFile(file, id);
    }

    @GetMapping("/download/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return materialsService.prepareDownloadResource(fileName);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<S3ObjectSummary>> listFiles() {
        return new ResponseEntity<>(materialsService.listFiles(), HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable UUID id) {
        materialsService.deleteFile(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFile(@RequestParam("file") MultipartFile file, @PathVariable("id") UUID id) throws IOException {
        materialsService.updateFile(file, id);
    }
}
