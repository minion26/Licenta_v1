package com.example.licentav1.service.impl;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.domain.Lectures;
import com.example.licentav1.domain.Materials;
import com.example.licentav1.repository.LecturesRepository;
import com.example.licentav1.repository.MaterialsRepository;
import com.example.licentav1.service.MaterialsService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MaterialsServiceImpl implements MaterialsService {
    private S3Service s3Service;
    private MaterialsRepository materialsRepository;
    private LecturesRepository lecturesRepository;

    public MaterialsServiceImpl(S3Service s3Service, MaterialsRepository materialsRepository, LecturesRepository lecturesRepository) {
        this.s3Service = s3Service;
        this.materialsRepository = materialsRepository;
        this.lecturesRepository = lecturesRepository;
    }

    @Override
    public void uploadFile(MultipartFile file, UUID id) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        String fileUrl = s3Service.uploadFile(file, metadata);

        Materials material = new Materials();
        material.setName(file.getOriginalFilename());
        material.setFileUrl(fileUrl);

        Lectures lecture = lecturesRepository.findById(id).orElse(null);
        if (lecture != null) {
            material.setLectures(lecture);
        }else{
            throw new RuntimeException("Lecture not found");
        }

        materialsRepository.save(material);
    }

    @Override
    public S3Object loadFileAsResource(String fileName) throws IOException {
        S3Object s3Object =  s3Service.downloadFile(fileName);
        if (s3Object == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        return s3Object;
    }

    @Override
    public ResponseEntity<Resource> prepareDownloadResource(String fileName) throws IOException {

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        try {
            // Load file as Resource
            S3Object s3Object = this.loadFileAsResource(fileName);

            Resource resource = new InputStreamResource(s3Object.getObjectContent());

            // Extract file extension
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

            // Set content type based on file extension
            String contentType = "application/octet-stream"; // default content type
            if ("pdf".equalsIgnoreCase(fileExtension)) {
                contentType = "application/pdf";
            } else if ("png".equalsIgnoreCase(fileExtension)) {
                contentType = "image/png";
            } else if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
                contentType = "image/jpeg";
            } else if ("doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
                contentType = "application/msword";
            } else if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
                contentType = "application/vnd.ms-excel";
            } else if ("ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
                contentType = "application/vnd.ms-powerpoint";
            } else if ("zip".equalsIgnoreCase(fileExtension)) {
                contentType = "application/zip";
            } else if ("txt".equalsIgnoreCase(fileExtension)) {
                contentType = "text/plain";
            } else if ("py".equalsIgnoreCase(fileExtension)) {
                contentType = "text/x-python";
            } else if ("java".equalsIgnoreCase(fileExtension)) {
                contentType = "text/x-java-source";
            } else if ("cpp".equalsIgnoreCase(fileExtension)) {
                contentType = "text/x-c++src";
            } else if ("c".equalsIgnoreCase(fileExtension)) {
                contentType = "text/x-csrc";
            }

            // Get S3Object metadata
            ObjectMetadata metadata = s3Object.getObjectMetadata();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.attachment().filename(resource.getFilename()).build());
            headers.setContentLength(metadata.getContentLength());
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());

            // Add all user metadata to the headers
            for (Map.Entry<String, String> entry : metadata.getUserMetadata().entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch(AmazonS3Exception e) {
            throw new FileNotFoundException("File not found: " + fileName);
        } catch (IOException e) {
            throw new IOException("Error while preparing download resource: " + e.getMessage());
        }
    }

    @Override
    public List<S3ObjectSummary> listFiles() {
        return s3Service.listObjects();
    }

    @Override
    public void deleteFile(UUID id) {
        Materials material = materialsRepository.findById(id).orElseThrow(() -> new RuntimeException("Material not found"));
        if (material != null) {
            s3Service.deleteFile(material.getName());
            materialsRepository.delete(material);
        }else{
            throw new RuntimeException("Material not found");
        }
    }

    @Override
    public void updateFile(MultipartFile file, UUID id) throws IOException {
        Materials material = materialsRepository.findById(id).orElseThrow(() -> new RuntimeException("Material not found"));
        if (material != null) {
            s3Service.deleteFile(material.getName());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String fileUrl = s3Service.uploadFile(file, metadata);
            material.setName(file.getOriginalFilename());
            material.setFileUrl(fileUrl);
            materialsRepository.save(material);
        }else{
            throw new RuntimeException("Material not found");
        }
    }


}
