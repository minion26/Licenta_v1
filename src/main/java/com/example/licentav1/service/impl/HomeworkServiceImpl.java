package com.example.licentav1.service.impl;

import com.example.licentav1.AWS.S3Service;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.HomeworkService;
import org.springframework.stereotype.Service;

@Service
public class HomeworkServiceImpl implements HomeworkService {
    private final HomeworkRepository homeworkRepository;
    private final LecturesRepository lecturesRepository;
    private final StudentsRepository studentsRepository;
    private final CoursesRepository coursesRepository;
    private final TeachersRepository teachersRepository;
    private final S3Service s3Service;

    public HomeworkServiceImpl(HomeworkRepository homeworkRepository, LecturesRepository lecturesRepository, StudentsRepository studentsRepository, CoursesRepository coursesRepository, TeachersRepository teachersRepository, S3Service s3Service) {
        this.homeworkRepository = homeworkRepository;
        this.lecturesRepository = lecturesRepository;
        this.studentsRepository = studentsRepository;
        this.coursesRepository = coursesRepository;
        this.teachersRepository = teachersRepository;
        this.s3Service = s3Service;
    }


}
