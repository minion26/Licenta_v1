package com.example.licentav1.service.impl;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.dto.HomeworkDTO;
import com.example.licentav1.dto.HomeworkGradeDTO;
import org.springframework.core.io.Resource;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.advice.exceptions.StorageException;
import com.example.licentav1.domain.*;
import com.example.licentav1.mapper.HomeworkMapper;
import com.example.licentav1.mapper.StudentHomeworkMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.HomeworkService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final StudentHomeworkRepository studentHomeworkRepository;
    private final HomeworkFilesRepository homeworkFilesRepository;
    private final StudentsRepository studentsRepository;
    private final S3Service s3Service;
    private final HomeworkAnnouncementsRepository homeworkAnnouncementsRepository;
    private final UsersRepository usersRepository;

    public HomeworkServiceImpl(HomeworkRepository homeworkRepository, StudentHomeworkRepository studentHomeworkRepository, StudentsRepository studentsRepository, HomeworkFilesRepository homeworkFilesRepository, S3Service s3Service, HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, UsersRepository usersRepository) {
        this.homeworkRepository = homeworkRepository;
        this.studentHomeworkRepository = studentHomeworkRepository;
        this.homeworkFilesRepository = homeworkFilesRepository;
        this.studentsRepository = studentsRepository;
        this.s3Service = s3Service;
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.usersRepository = usersRepository;
    }


    @Override
    public void uploadHomework(List<MultipartFile> files, UUID idHomeworkAnnouncement, UUID idStudent) throws IOException {
        HomeworkAnnouncements homeworkAnnouncements = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new RuntimeException("Homework not found"));
        // trebuie sa creez un studentHomework
        Students student = studentsRepository.findById(idStudent).orElseThrow(() -> new RuntimeException("Student not found"));

        StudentHomework studentHomework = studentHomeworkRepository.findByIdStudentAndIdHomeworkAnnouncement(idStudent, idHomeworkAnnouncement).orElse(null);
        // daca studentul a mai incarcat deja tema
        if (studentHomework != null) {
            throw new RuntimeException("Homework already uploaded");
        }

        Homework homework = HomeworkMapper.map(homeworkAnnouncements);

        // aici trebuie sa fac upload pe s3
        List<HomeworkFiles> homeworkFilesList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            try {
                String fileUrl = s3Service.uploadHomeworkFile(file);

                // creez un obiect HomeworkFiles
                HomeworkFiles homeworkFiles = new HomeworkFiles();
                homeworkFiles.setFileUrl(fileUrl);
                homeworkFiles.setHomework(homework);

                // adaug obiectul in lista
                homeworkFilesList.add(homeworkFiles);


            } catch (AmazonS3Exception e) {
                throw new StorageException("Error occurred while trying to upload file to S3 " + e.getMessage());
            } catch (IOException | InterruptedException e) {
                throw new IOException("Error while uploading file: " + e.getMessage());
            }
        }

        // adaug lista de fisiere la homework
        homework.setHomeworkFiles(homeworkFilesList);

        // salvez homework-ul
        homeworkRepository.save(homework);

        homeworkFilesRepository.saveAll(homeworkFilesList);


        // creez un obiect StudentHomework
        StudentHomework newStudentHomework = StudentHomeworkMapper.map(homeworkAnnouncements, student, homework);

        // salvez studentHomework-ul
        studentHomeworkRepository.save(newStudentHomework);
    }



    @Override
    public S3Object loadFileAsResource(String fileName) throws IOException {
        S3Object s3Object =  s3Service.downloadHomeworkFile(fileName);
        if (s3Object == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        return s3Object;
    }



    @Override
    public ResponseEntity<Resource> prepareDownloadHomeworkResource(String fileName) throws IOException {

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
            }else if ("html".equalsIgnoreCase(fileExtension)) {
                contentType = "text/html";
            } else if ("css".equalsIgnoreCase(fileExtension)) {
                contentType = "text/css";
            } else if ("js".equalsIgnoreCase(fileExtension)) {
                contentType = "application/javascript";
            } else if ("csv".equalsIgnoreCase(fileExtension)) {
                contentType = "text/csv";
            } else if ("xml".equalsIgnoreCase(fileExtension)) {
                contentType = "application/xml";
            } else if ("json".equalsIgnoreCase(fileExtension)) {
                contentType = "application/json";
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
        return s3Service.listHomeworks();
    }

    @Override
    @Transactional
    public void deleteHomeworkFile(UUID id) {
        HomeworkFiles homeworkFiles = homeworkFilesRepository.findById(id).orElseThrow(() -> new RuntimeException("Homework file not found"));

        if (homeworkFiles != null) {
            String name = homeworkFiles.getFileUrl().substring(homeworkFiles.getFileUrl().lastIndexOf("/") + 1);
            System.out.println(name);
            s3Service.deleteHomeworkFile(name);
            homeworkFilesRepository.delete(homeworkFiles);

            // daca nu mai exista niciun fisier pentru homework-ul respectiv, sterg si homework-ul si student-homework-ul

            if (homeworkFilesRepository.findAllByHomework(homeworkFiles.getHomework()).isEmpty()) {
                studentHomeworkRepository.deleteByHomework(homeworkFiles.getHomework());

                homeworkRepository.delete(homeworkFiles.getHomework());

            }

        }else{
            throw new RuntimeException("Homework file not found");
        }
    }

    @Override
    public void updateHomeworkFile(MultipartFile file, UUID id) {
        HomeworkFiles homeworkFiles = homeworkFilesRepository.findById(id).orElseThrow(() -> new RuntimeException("Homework file not found"));

        if (homeworkFiles != null){
            // iau numele si sterg din fata lui bucketName2
            String name = homeworkFiles.getFileUrl().substring(homeworkFiles.getFileUrl().lastIndexOf("/") + 1);

            // sterg fisierul vechi
            s3Service.deleteHomeworkFile(name);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            try {
                String fileUrl = s3Service.uploadHomeworkFile(file);
                homeworkFiles.setFileUrl(fileUrl);
                homeworkFilesRepository.save(homeworkFiles);
            }catch (IOException | InterruptedException e){
                throw new RuntimeException("Error while uploading file: " + e.getMessage());
            }

        }else{
            throw new RuntimeException("Homework file not found");
        }
    }

    @Override
    public List<HomeworkDTO> getAllHomeworks(UUID idHomeworkAnnouncement) {
        // am enuntul temei
        HomeworkAnnouncements homeworkAnnouncements = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new RuntimeException("Homework not found"));

        // am lista de studenti care au incarcat tema
        List<StudentHomework> studentHomeworkList = studentHomeworkRepository.findAllByIdHomeworkAnnouncement(idHomeworkAnnouncement);

        // vreau toate temele incarcate de studenti
        List<HomeworkDTO> homeworkDTOList = new ArrayList<>();

        for (StudentHomework studentHomework : studentHomeworkList) {
            Students student = studentHomework.getStudent();
            Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new RuntimeException("User not found"));
            List<HomeworkFiles> homeworkFiles = studentHomework.getHomework().getHomeworkFiles();
            Homework homework = studentHomework.getHomework();

            HomeworkDTO homeworkDTO = new HomeworkDTO();

            homeworkDTO.setIdHomework(studentHomework.getHomework().getIdHomework());
            homeworkDTO.setIdStudent(student.getIdUsers());
            homeworkDTO.setNrMatricol(student.getNrMatriculation());
            homeworkDTO.setFirstNameStudent(user.getFirstName());
            homeworkDTO.setLastNameStudent(user.getLastName());
            homeworkDTO.setGrade(homework.getGrade());
            homeworkDTO.setUploadDate(homework.getDueDate());

            List<String> filesNames = new ArrayList<>();
            for (HomeworkFiles homeworkFile : homeworkFiles) {
                filesNames.add(homeworkFile.getFileUrl());
            }

            homeworkDTO.setFileName(filesNames);

            homeworkDTOList.add(homeworkDTO);
        }

        return homeworkDTOList;
    }

    @Override
    public void gradeHomework(UUID idHomework, HomeworkGradeDTO homeworkGradeDTO) {
        Homework homework = homeworkRepository.findById(idHomework).orElseThrow(() -> new RuntimeException("Homework not found"));

        homework.setGrade(homeworkGradeDTO.getGrade());
        homeworkRepository.save(homework);
    }
}
