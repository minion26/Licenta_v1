package com.example.licentav1.service;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MaterialsService {
    void uploadFile(List<MultipartFile> file, UUID id) throws IOException;
    S3Object loadFileAsResource(String fileName) throws IOException;
    ResponseEntity<Resource> prepareDownloadResource(String fileName) throws IOException;

    List<S3ObjectSummary> listFiles();

    void deleteFile(UUID id);

    void updateFile(MultipartFile file, UUID id) throws IOException;
}
