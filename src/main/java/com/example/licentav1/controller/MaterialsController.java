package com.example.licentav1.controller;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.domain.Materials;
import com.example.licentav1.dto.MaterialsDTO;
import com.example.licentav1.dto.MaterialsInfoDTO;
import com.example.licentav1.service.MaterialsService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @PostMapping(path="/upload/{idLecture}")
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFile(@RequestParam("file") List<MultipartFile> file, @PathVariable("idLecture") UUID idLecture, @RequestParam("materialsDTO") String materialsDTOJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialsDTO materialsDTO = objectMapper.readValue(materialsDTOJson, MaterialsDTO.class);
        materialsService.uploadFile(file, idLecture, materialsDTO);
    }

    @GetMapping("/download/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws IOException {
        return materialsService.prepareDownloadResource(fileName);
    }

    @GetMapping("/list-by-type/{id}/{type}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<S3ObjectSummary>> listFilesByType(@PathVariable UUID id, @PathVariable String type) {
        return new ResponseEntity<>(materialsService.listFilesByType(id, type), HttpStatus.OK);
    }

//    @GetMapping("/load/{fileName}")
//    @ResponseStatus(HttpStatus.OK)
//    public String loadFile(@PathVariable String fileName) throws IOException {
//        return materialsService.loadFileAsURL(fileName);
//    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<S3ObjectSummary>> listFiles() {
        return new ResponseEntity<>(materialsService.listFiles(), HttpStatus.OK);
    }

    @GetMapping("/get-type/idLecture={id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<String>> getMaterialType(@PathVariable UUID id) {
        List<String> materialType = materialsService.getMaterialTypeById(id);
        return new ResponseEntity<>(materialType, HttpStatus.OK);
    }

    @GetMapping("/list/{key}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String key) throws IOException {
        return materialsService.getFile(key);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable UUID id) {
        materialsService.deleteFile(id);
    }

    @PatchMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFile(@RequestParam("file") MultipartFile file, @PathVariable("id") UUID id, @RequestParam("materialsDTO") String materialsDTOJson) throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        MaterialsDTO materialsDTO = objectMapper.readValue(materialsDTOJson, MaterialsDTO.class);
        materialsService.updateFile(file, id, materialsDTO);
    }

    @GetMapping("/get/idLectures={id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MaterialsInfoDTO> getMaterialsByIdLectures(@PathVariable UUID id) {
        return materialsService.getMaterialsByIdLectures(id);
    }
}
