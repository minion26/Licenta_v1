package com.example.licentav1.service.impl;

import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.licentav1.AWS.DocToPdfConverter;
import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.advice.exceptions.*;
import com.example.licentav1.config.JwtService;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.MaterialsDTO;
import com.example.licentav1.dto.MaterialsInfoDTO;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.MaterialsService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class MaterialsServiceImpl implements MaterialsService {
    private final StudentsFollowCoursesRepository studentsFollowCoursesRepository;
    private S3Service s3Service;
    private MaterialsRepository materialsRepository;
    private LecturesRepository lecturesRepository;
    private final JwtService jwtService;
    private final TeachersRepository teacherRepository;
    private final HttpServletRequest request;
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final StudentsRepository studentRepository;

    private final DocToPdfConverter docToPdfConverter;

    public MaterialsServiceImpl(S3Service s3Service, MaterialsRepository materialsRepository, LecturesRepository lecturesRepository, JwtService jwtService, TeachersRepository teacherRepository, HttpServletRequest request, CoursesRepository coursesRepository, DidacticRepository didacticRepository, StudentsRepository studentRepository, StudentsFollowCoursesRepository studentsFollowCoursesRepository, DocToPdfConverter docToPdfConverter) {
        this.s3Service = s3Service;
        this.materialsRepository = materialsRepository;
        this.lecturesRepository = lecturesRepository;
        this.jwtService = jwtService;
        this.teacherRepository = teacherRepository;
        this.request = request;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.studentRepository = studentRepository;
        this.studentsFollowCoursesRepository = studentsFollowCoursesRepository;
        this.docToPdfConverter = docToPdfConverter;
    }

    @Override
    public void uploadFile(List<MultipartFile> file, UUID id, MaterialsDTO materialsDTO) throws IOException {
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

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID idToken = jwtService.getUserIdFromToken(token);
//        System.out.println("id from token: " + idToken);
        Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        Lectures lectures = lecturesRepository.findById(id).orElseThrow(() -> new RuntimeException("Lecture not found"));

        //am lecture iau cursul
        Courses course = lectures.getCourses();

        //am cursul si profesorul iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to see upload to this course");
        }
//        else{
//            System.out.println("You are allowed to see this course");
//        }


        for (MultipartFile f : file){
            if (f.isEmpty()) {
                throw new FileException("File is empty");
            }

            try {
//                Lectures lectures = lecturesRepository.findById(id).orElseThrow(() -> new RuntimeException("Lecture not found"));
                if (lectures != null) {
                    if (materialsRepository.existsByLecturesAndName(lectures.getIdLecture(), f.getOriginalFilename())) {
                        throw new FileException("File already exists for this lecture");
                    }
                }


                MultipartFile fileToUpload = f; // Create a new variable to hold the file to upload

                // Check if the file is a doc or docx file
                String originalFilename = f.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                //delete the first character
                extension = extension.substring(1);

                File tempFile = null;

                //if the file is a doc or docx file, convert it to a PDF
                if ("doc".equalsIgnoreCase(extension) || "docx".equalsIgnoreCase(extension)) {
                    try {
                        System.out.println("Converting file: " + f.getOriginalFilename() + " to PDF");

                        tempFile = File.createTempFile("temp-file-name", "." + extension);
                        f.transferTo(tempFile);
                        File pdfFile = File.createTempFile("temp-file-name", ".pdf");

                        System.out.println("Calling convertDocToPdf"); // Log before calling convertDocToPdf

                        docToPdfConverter.convertDocToPdf(tempFile, pdfFile);

                        System.out.println("Finished calling convertDocToPdf"); // Log after calling convertDocToPdf

                        // Create a new MockMultipartFile from the PDF file
                        f = new MockMultipartFile("file", originalFilename.substring(0, originalFilename.lastIndexOf(".")) + ".pdf", "application/pdf", new FileInputStream(pdfFile));

                        System.out.println("File converted successfully: " + f.getOriginalFilename());
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
                String fileUrl = s3Service.uploadFile(f);


                Materials material = new Materials();
                material.setName(f.getOriginalFilename());
                material.setFileUrl(fileUrl);
                material.setMaterialType(materialsDTO.getMaterialType());

                Lectures lecture = lecturesRepository.findById(id).orElse(null);
                if (lecture != null) {
                    material.setLectures(lecture);
                } else {
                    throw new RuntimeException("Lecture not found");
                }

                materialsRepository.save(material);
            }catch (AmazonS3Exception e){
                throw new StorageException("Error occurred while trying to upload file to S3 " + e.getMessage());
            } catch (IOException | InterruptedException e) {
                throw new IOException("Error while uploading file: " + e.getMessage());
            }
        }

    }

    @Override
    public S3Object loadFileAsResource(String fileName) throws IOException {
        S3Object s3Object =  s3Service.downloadFile(fileName);
        if (s3Object == null) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        byte[] content;
        try (S3ObjectInputStream stream = s3Object.getObjectContent()) {
            // Read the entire input stream to ensure all bytes are read
            content = IOUtils.toByteArray(stream);
            System.out.println("Content: " + Arrays.toString(content));
        } catch (IOException e) {
            throw new IOException("Error while reading file: " + e.getMessage());
        }

        // Create a new InputStream from the byte array
        InputStream is = new ByteArrayInputStream(content);

        // Create a new S3Object using the new InputStream
        S3Object newS3Object = new S3Object();
        newS3Object.setObjectContent(is);
        newS3Object.setBucketName(s3Object.getBucketName());
        newS3Object.setKey(s3Object.getKey());

        return newS3Object;

//        return s3Object;
    }

    @Override
    public ResponseEntity<Resource> prepareDownloadResource(String fileName) throws IOException {

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        try {
            // Load file as Resource
            S3Object s3Object = this.loadFileAsResource(fileName);

            // Get the byte array from the S3Object
            byte[] content;
            try (S3ObjectInputStream stream = s3Object.getObjectContent()) {
                content = IOUtils.toByteArray(stream);
            }

            // Create a new InputStream from the byte array
            InputStream is = new ByteArrayInputStream(content);

            // Create a new Resource from the InputStream
            Resource resource = new InputStreamResource(is);

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

            String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.attachment().filename(encodedFilename).build());
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
    public void updateFile(MultipartFile file, UUID id, MaterialsDTO materialsDTO) throws IOException, InterruptedException {
        Materials material = materialsRepository.findById(id).orElseThrow(() -> new RuntimeException("Material not found"));
        if (material != null) {
            s3Service.deleteFile(material.getName());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            String fileUrl = s3Service.uploadFile(file);
            material.setName(file.getOriginalFilename());
            material.setFileUrl(fileUrl);
            material.setMaterialType(materialsDTO.getMaterialType());
            materialsRepository.save(material);
        }else{
            throw new RuntimeException("Material not found");
        }
    }

    @Override
    public List<String> getMaterialTypeById(UUID id) {
        List<String> types = new ArrayList<>();
        Lectures lectures = lecturesRepository.findById(id).orElseThrow(() -> new RuntimeException("Lecture not found"));
        if (lectures != null) {
            List<Materials> materials = materialsRepository.findByIdLectures(id).orElseThrow(() -> new RuntimeException("Materials not found"));
            for(Materials material : materials){
                types.add(material.getMaterialType());
            }
        }
        return types;
    }

    @Override
    public List<S3ObjectSummary> listFilesByType(UUID id, String type) {
        //aici id e idLectures
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

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID idToken = jwtService.getUserIdFromToken(token);
        String role = jwtService.extractRole(token);
//        System.out.println("id from token: " + idToken);

        List<S3ObjectSummary> s3ObjectSummaries = new ArrayList<>();
        Lectures lectures = lecturesRepository.findById(id).orElseThrow(() -> new RuntimeException("Lecture not found"));

        if(role.equals("TEACHER")){
            Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

            //am lecture iau cursul
            Courses course = lectures.getCourses();
            //am cursul si profesorul iau didactic
            Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

            if(didactic == null) {
                throw new NonAllowedException("You are not allowed to see this material");
            }
//            else{
//                System.out.println("You are allowed to see this course");
//            }

        }else if(role.equals("STUDENT")){
            Students studentFromJwt = studentRepository.findById(idToken).orElseThrow(() -> new StudentNotFoundException("Student not found"));

            StudentsFollowCourses studentsFollowCourses = studentsFollowCoursesRepository.findByStudentAndCourse(studentFromJwt.getIdUsers(), lectures.getCourses().getIdCourses()).orElse(null);
            if(studentsFollowCourses == null) {
                throw new NonAllowedException("You are not allowed to see this material");
            }
//            else{
//                System.out.println("You are allowed to see this course");
//            }
        }else{
            throw new NonAllowedException("You are not allowed to see this material");
        }



        if (lectures != null) {
            List<Materials> materials = materialsRepository.findByIdLectures(id).orElseThrow(() -> new RuntimeException("Materials not found"));
            for(Materials material : materials){
                if (material.getMaterialType().equals(type)){
                    S3ObjectSummary s3ObjectSummary = new S3ObjectSummary();
                    s3ObjectSummary.setKey(material.getName());
                    s3ObjectSummary.setBucketName("licenta-bucket");
                    s3ObjectSummaries.add(s3ObjectSummary);
                }
            }
        }
        return s3ObjectSummaries;
    }

    @Override
    public ResponseEntity<InputStreamResource> getFile(String key) throws IOException {
        S3Object s3Object = s3Service.getObject(key);
        // Get the byte array from the S3Object
        byte[] content;
        try (S3ObjectInputStream stream = s3Object.getObjectContent()) {
            content = IOUtils.toByteArray(stream);
        }

        // Create a new InputStream from the byte array
        InputStream is = new ByteArrayInputStream(content);

        // Create a new Resource from the InputStream
        InputStreamResource resource = new InputStreamResource(is);
        // Extract file extension
        String fileExtension = key.substring(key.lastIndexOf(".") + 1);

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
        } else if ("html".equalsIgnoreCase(fileExtension)) {
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

        String encodedFilename = URLEncoder.encode(key, StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(resource);
    }

    @Override
    public List<MaterialsInfoDTO> getMaterialsByIdLectures(UUID id) {
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

        if(token == null) {
            throw new RuntimeException("Token not found");
        }

        UUID idToken = jwtService.getUserIdFromToken(token);
//        System.out.println("id from token: " + idToken);
        Teachers teacherFromJwt = teacherRepository.findById(idToken).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        // Verificați dacă cursul există
        Lectures lecture = lecturesRepository.findById(id).orElseThrow(() -> new RuntimeException("Lecture not found"));

        //am lecture, iau cursul
        Courses course = lecture.getCourses();

        //am cursul și profesorul, iau didactic
        Didactic didactic = didacticRepository.findByTeacherAndCourse(teacherFromJwt.getIdUsers(), course.getIdCourses()).orElse(null);

        if(didactic == null) {
            throw new NonAllowedException("You are not allowed to see this material");
        }
//        else {
//            System.out.println("You are allowed to see this course");
//        }

        List<MaterialsInfoDTO> materialsInfoDTOS = new ArrayList<>();
        if (lecture != null) {
            List<Materials> materials = materialsRepository.findByIdLectures(id).orElseThrow(() -> new RuntimeException("Materials not found"));
            for (Materials material : materials) {
                MaterialsInfoDTO materialsInfoDTO = new MaterialsInfoDTO();
                materialsInfoDTO.setIdMaterial(material.getIdMaterial());
                materialsInfoDTO.setName(material.getName());
                materialsInfoDTOS.add(materialsInfoDTO);
            }
        }
        return materialsInfoDTOS;
    }


}
