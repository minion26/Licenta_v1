package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Students;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.service.StudentsService;
import org.springframework.stereotype.Service;

@Service
public class StudentsServiceImpl implements StudentsService {
    private final StudentsRepository studentsRepository;

    public StudentsServiceImpl(StudentsRepository studentsRepository) {
        this.studentsRepository = studentsRepository;
    }

    @Override
    public Iterable<Students> getStudents() {
        return studentsRepository.findAll();
    }
}
