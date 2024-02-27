package com.example.licentav1.controller;

import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.service.MaterialsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/materials")
public class MaterialsController {

    private MaterialsService materialsService;

    public MaterialsController(MaterialsService materialsService) {
        this.materialsService = materialsService;
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        materialsService.uploadFile(file);
    }


}
