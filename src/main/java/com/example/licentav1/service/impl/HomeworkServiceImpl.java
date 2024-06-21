package com.example.licentav1.service.impl;

import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.licentav1.AWS.DocToPdfConverter;
import com.example.licentav1.advice.exceptions.NonAllowedException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.dto.HomeworkDTO;
import com.example.licentav1.dto.HomeworkGradeDTO;
import com.example.licentav1.email.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.beans.Transient;
import java.io.File;
import java.io.FileInputStream;
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
    private final HttpServletRequest request;
    private final TeachersRepository teachersRepository;
    private final JwtService jwtService;
    private final DidacticRepository didacticRepository;
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private final FeedbackRepository feedbackRepository;
    private final EmailService emailService;
    private final DocToPdfConverter docToPdfConverter;

    public HomeworkServiceImpl(HomeworkRepository homeworkRepository, StudentHomeworkRepository studentHomeworkRepository, StudentsRepository studentsRepository, HomeworkFilesRepository homeworkFilesRepository, S3Service s3Service, HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, UsersRepository usersRepository, HttpServletRequest request, TeachersRepository teachersRepository, JwtService jwtService, DidacticRepository didacticRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository, FeedbackRepository feedbackRepository, EmailService emailService, DocToPdfConverter docToPdfConverter) {
        this.homeworkRepository = homeworkRepository;
        this.studentHomeworkRepository = studentHomeworkRepository;
        this.homeworkFilesRepository = homeworkFilesRepository;
        this.studentsRepository = studentsRepository;
        this.s3Service = s3Service;
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.usersRepository = usersRepository;
        this.request = request;
        this.teachersRepository = teachersRepository;
        this.jwtService = jwtService;
        this.didacticRepository = didacticRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.feedbackRepository = feedbackRepository;
        this.emailService = emailService;
        this.docToPdfConverter = docToPdfConverter;
    }


    @Override
    public void uploadHomework(List<MultipartFile> files, UUID idHomeworkAnnouncement, UUID idStudent) throws IOException {
        HomeworkAnnouncements homeworkAnnouncements = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new RuntimeException("Homework not found"));
        // trebuie sa creez un studentHomework
        Students student = studentsRepository.findById(idStudent).orElseThrow(() -> new RuntimeException("Student not found"));

        StudentHomework studentHomework = studentHomeworkRepository.findByIdStudentAndIdHomeworkAnnouncement(idStudent, idHomeworkAnnouncement).orElse(null);
        // daca studentul a mai incarcat deja tema
        if (studentHomework != null) {
            throw new NonAllowedException("Homework already uploaded");
        }

        Homework homework = HomeworkMapper.map(homeworkAnnouncements);

        // aici trebuie sa fac upload pe s3
        List<HomeworkFiles> homeworkFilesList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            try {
                MultipartFile fileToUpload = file; // Create a new variable to hold the file to upload

                // Check if the file is a doc or docx file
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                //delete the first character
                extension = extension.substring(1);

                File tempFile = null;

                //if the file is a doc or docx file, convert it to a PDF
                if ("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)) {
                    try {
                        System.out.println("Converting file: " + file.getOriginalFilename() + " to PDF");

                        tempFile = File.createTempFile("temp-file-name", "." + extension);
                        file.transferTo(tempFile);
                        File pdfFile = File.createTempFile("temp-file-name", ".pdf");

                        System.out.println("Calling convertDocToPdf"); // Log before calling convertDocToPdf

                        docToPdfConverter.convertDocToPdf(tempFile, pdfFile);

                        System.out.println("Finished calling convertDocToPdf"); // Log after calling convertDocToPdf

                        // Create a new MockMultipartFile from the PDF file
                        file = new MockMultipartFile("file", originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".pdf", "application/pdf", new FileInputStream(pdfFile));

                        System.out.println("File converted successfully: " + file.getOriginalFilename());
                    } catch (Exception e) {
                        System.out.println("Exception while converting doc to pdf: " + e.getMessage());
                    }finally {
                        // Add a null check before calling delete
                        if (tempFile != null) {
                            tempFile.delete();
                        }
                    }
                }

                // Upload the file to S3
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
        System.out.println("Downloading file: " + fileName);
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
            System.out.println("DELETE: " + homeworkFiles.getFileUrl());
            s3Service.deleteHomeworkFile(name);
            // Get the Homework entity that references the HomeworkFiles
            Homework homework = homeworkFiles.getHomework();

            // Remove the association
            homework.setHomeworkFiles(null);
            homeworkRepository.save(homework);

            // Now you can delete the HomeworkFiles
            homeworkFilesRepository.delete(homeworkFiles);

            // If there are no more files for the respective homework, delete the homework and student-homework as well
            if (homeworkFilesRepository.findAllByHomework(homework).isEmpty()) {
                studentHomeworkRepository.deleteByHomework(homework);

                // Also delete it from feedback
                feedbackRepository.deleteByHomework(homework.getIdHomework());

                homeworkRepository.delete(homework);
            }else{
                System.out.println("Homework has more files");
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
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
//        System.out.println("id from token: " + id);
        Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));


        // am enuntul temei
        HomeworkAnnouncements homeworkAnnouncements = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new RuntimeException("Homework not found"));

        //iau lecture
        Lectures lecture = homeworkAnnouncements.getLectures();
        //iau cursul
        Courses course = lecture.getCourses();
        //iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if (didactic == null) {
            throw new NonAllowedException("You are not allowed to see the submissions for this course");
        }
//        else{
//            System.out.println("You are allowed to see this course");
//        }

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

        //sa trimit email studentului ca tema a fost corectata
        StudentHomework studentHomework = studentHomeworkRepository.findByIdHomework(idHomework).orElseThrow(() -> new RuntimeException("Student homework not found"));
        //iau studentul
        Students student = studentHomework.getStudent();
        //iau userul
        Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new RuntimeException("User not found"));
        //trimitem email
        emailService.sendGradeHomeworkStyle(user.getFacultyEmail(), homework.getHomeworkAnnouncements().getTitle(), homework.getHomeworkAnnouncements().getLectures().getCourses().getName(), homework.getGrade());
    }

    @Override
    public HomeworkDTO getHomework(UUID idHomework) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        String role = jwtService.extractRole(token);
//        System.out.println("id from token: " + id);

        Homework homework = homeworkRepository.findById(idHomework).orElseThrow(() -> new NonAllowedException("Homework not found"));
        HomeworkAnnouncements homeworkAnnouncements = homework.getHomeworkAnnouncements();
        Lectures lecture = homeworkAnnouncements.getLectures();
        Courses course = lecture.getCourses();

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if (didactic == null) {
                throw new NonAllowedException("You are not allowed to see the submissions for this course");
            }
//            else{
//                System.out.println("You are allowed to see this course");
//            }

        }else if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if (studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to upload the homework for this course");
            }
//            else{
//                System.out.println("You are allowed to upload the homework for this course");
//            }
        }




        List<HomeworkFiles> homeworkFiles = homework.getHomeworkFiles();

        StudentHomework studentHomework = studentHomeworkRepository.findByIdHomework(idHomework).orElseThrow(() -> new RuntimeException("Student homework not found"));
        Students student = studentHomework.getStudent();
        Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new RuntimeException("User not found"));

        HomeworkDTO homeworkDTO = new HomeworkDTO();

        homeworkDTO.setIdHomework(homework.getIdHomework());
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

        return homeworkDTO;
    }

    @Override
    public ResponseEntity<Resource> getFile(String name) {
        S3Object s3Object = s3Service.getObjectHomework(name);
        InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

        //extract file extension
        String fileExtension = name.substring(name.lastIndexOf(".") + 1);

        //set content type based on file extension
        String contentType = "application/octet-stream"; //default content type
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

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }

    @Override
    public ResponseEntity<UUID> getHomeworkIdFile(UUID idHomework, String name) {
        //vreau sa verific daca profesorul preda la cursul respectiv
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID id = jwtService.getUserIdFromToken(token);
        String role = jwtService.extractRole(token);
//        System.out.println("id from token: " + id);

        Homework homework = homeworkRepository.findById(idHomework).orElseThrow(() -> new RuntimeException("Homework not found"));
        HomeworkAnnouncements homeworkAnnouncements = homework.getHomeworkAnnouncements();
        Lectures lecture = homeworkAnnouncements.getLectures();
        Courses course = lecture.getCourses();

        if(role.equals("STUDENT")){
            Students studentFromJwt = studentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if (studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to upload the homework for this course");
            }
//            else{
//                System.out.println("You are allowed to upload the homework for this course");
//            }
        }


        List<HomeworkFiles> homeworkFiles = homework.getHomeworkFiles();

        StudentHomework studentHomework = studentHomeworkRepository.findByIdHomework(idHomework).orElseThrow(() -> new RuntimeException("Student homework not found"));
        Students student = studentHomework.getStudent();
        Users user = usersRepository.findById(student.getIdUsers()).orElseThrow(() -> new RuntimeException("User not found"));



        for (HomeworkFiles homeworkFile : homeworkFiles) {
            String fileUrl = homeworkFile.getFileUrl();
            String nameFile = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            System.out.println(nameFile);
            if(nameFile.equals(name)){
                return new ResponseEntity<>(homeworkFile.getIdHomeworkFiles(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
