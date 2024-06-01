package com.example.licentav1.service;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.dto.HomeworkDTO;
import com.example.licentav1.dto.HomeworkGradeDTO;
import org.springframework.core.io.Resource;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface HomeworkService {

    void uploadHomework(List<MultipartFile> file, UUID idHomeworkAnnouncement, UUID idStudent) throws IOException;

    S3Object loadFileAsResource(String fileName) throws IOException;

    ResponseEntity<Resource> prepareDownloadHomeworkResource(String fileName) throws IOException;

    List<S3ObjectSummary> listFiles();

    void deleteHomeworkFile(UUID id);

    void updateHomeworkFile(MultipartFile file, UUID id);

    List<HomeworkDTO> getAllHomeworks(UUID idHomeworkAnnouncement);

    void gradeHomework(UUID idHomework, HomeworkGradeDTO homeworkGradeDTO);

    HomeworkDTO getHomework(UUID idHomework);

    ResponseEntity<Resource> getFile(String name);
}
