package com.example.licentav1.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MaterialsService {
    void uploadFile(MultipartFile file) throws IOException;


}
