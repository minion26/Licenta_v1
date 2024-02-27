package com.example.licentav1.service.impl;

import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.repository.MaterialsRepository;
import com.example.licentav1.service.MaterialsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class MaterialsServiceImpl implements MaterialsService {
    private S3Service s3Service;

    private MaterialsRepository materialsRepository;

    public MaterialsServiceImpl(S3Service s3Service, MaterialsRepository materialsRepository) {
        this.s3Service = s3Service;
        this.materialsRepository = materialsRepository;
    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException {
        s3Service.uploadFile(file);

    }


}
