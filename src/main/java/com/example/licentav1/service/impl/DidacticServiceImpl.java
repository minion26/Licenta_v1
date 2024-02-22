package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Courses;
import com.example.licentav1.domain.Didactic;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.repository.CoursesRepository;
import com.example.licentav1.repository.DidacticRepository;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.service.DidacticService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DidacticServiceImpl implements DidacticService {

    private final DidacticRepository didacticRepository;
    private final CoursesRepository coursesRepository;
    private final TeachersRepository teacherRepository;

    public DidacticServiceImpl(DidacticRepository didacticRepository, CoursesRepository coursesRepository, TeachersRepository teacherRepository) {
        this.didacticRepository = didacticRepository;
        this.coursesRepository = coursesRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public void createDidactic(UUID courseId, UUID teacherId) {
        Optional<Teachers> teachers = teacherRepository.findById(teacherId);
        Optional<Courses> courses = coursesRepository.findById(courseId);

        if (teachers.isEmpty() || courses.isEmpty()) {
            throw new RuntimeException("Teacher or course not found");
        }

        Didactic didactic = new Didactic(teachers.get(), courses.get());

        try{
            didacticRepository.save(didactic);
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }
    }
}
