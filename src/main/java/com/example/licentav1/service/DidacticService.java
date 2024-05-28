package com.example.licentav1.service;

import com.example.licentav1.dto.DidacticDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface DidacticService {
    void createDidactic(UUID courseId, UUID teacherId);

    void uploadFile(MultipartFile file) throws IOException;

    List<DidacticDTO> getAllDidactic();

    void deleteDidactic(UUID id);

    void updateDidactic(UUID id, DidacticDTO didacticDTO);

    List<DidacticDTO> getDidacticByCourse(UUID idCourse);
}
