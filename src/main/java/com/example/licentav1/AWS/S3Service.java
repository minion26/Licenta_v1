package com.example.licentav1.AWS;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public List<S3ObjectSummary> listObjects() {
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries();
    }

    public String uploadFile(MultipartFile file) throws IOException, InterruptedException {
        /*String s3Path = bucketName + "/" + file.getOriginalFilename();
        try{
            s3Client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata));
        } catch (AmazonS3Exception e) {
            throw new AmazonS3Exception("Failed to upload file to S3", e);
        }
        return s3Path;*/

        File tempFile = File.createTempFile("temp-file-name", ".tmp");
        file.transferTo(tempFile);

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(this.getS3Client())
                .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
                .build();

        Upload upload = tm.upload(bucketName, file.getOriginalFilename(), tempFile);
        upload.waitForCompletion();

        tempFile.delete();

        String fileUrl = bucketName + "/" + file.getOriginalFilename();
        return fileUrl;
    }

    public S3Object downloadFile(String fileName) {

        S3Object s3Object =  s3Client.getObject(new GetObjectRequest(bucketName, fileName));
        if (s3Object == null) {
            throw new AmazonS3Exception("File not found: " + fileName);
        }
        return s3Object;

    }

    public void deleteFile(String fileName) {
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

}