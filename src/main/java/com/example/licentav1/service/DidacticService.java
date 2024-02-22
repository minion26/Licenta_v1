package com.example.licentav1.service;

import java.util.UUID;

public interface DidacticService {
    void createDidactic(UUID courseId, UUID teacherId);
}
