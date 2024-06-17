package com.example.licentav1.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.domain.Materials;
import com.example.licentav1.dto.MaterialsDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import com.example.licentav1.dto.MaterialsInfoDTO;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MaterialsService {
    void uploadFile(List<MultipartFile> file, UUID id, MaterialsDTO materialsDTO) throws IOException;
    S3Object loadFileAsResource(String fileName) throws IOException;
    ResponseEntity<Resource> prepareDownloadResource(String fileName) throws IOException;

    List<S3ObjectSummary> listFiles();

    void deleteFile(UUID id);

    void updateFile(MultipartFile file, UUID id, MaterialsDTO materialsDTO) throws IOException, InterruptedException;

    List<String> getMaterialTypeById(UUID id);

    List<S3ObjectSummary> listFilesByType(UUID id, String type);


    ResponseEntity<InputStreamResource> getFile(String key) throws IOException;

    List<MaterialsInfoDTO> getMaterialsByIdLectures(UUID id);
}
