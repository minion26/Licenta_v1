package com.example.licentav1.service.impl;

import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.ExamRepository;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.service.ExamService;
import org.springframework.stereotype.Service;


@Service
public class ExamServiceImpl implements ExamService {
    private ExamRepository examRepository;
    private CoursesRepository coursesRepository;
    private TeachersRepository teachersRepository;

    public ExamServiceImpl(ExamRepository examRepository, CoursesRepository coursesRepository, TeachersRepository teachersRepository) {
        this.examRepository = examRepository;
        this.coursesRepository = coursesRepository;
        this.teachersRepository = teachersRepository;
    }


}
