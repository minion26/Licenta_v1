package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import com.example.licentav1.mapper.UsersMapper;
import com.example.licentav1.mapper.StudentsMapper;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.StudentsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StudentsServiceImpl implements StudentsService {
    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;



    public StudentsServiceImpl(StudentsRepository studentsRepository, UsersRepository usersRepository) {
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Iterable<Students> getStudents() {
        return studentsRepository.findAll();
    }

    @Override
    public void createStudent(StudentsCreationDTO studentsCreationDTO) {
        Users users;
        UUID idUser = null;

        try{
            users = UsersMapper.fromStudentCreationDTO(studentsCreationDTO);
            usersRepository.save(users);
            idUser = users.getIdUsers();
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }

        try{
            Students students = Students.builder()
                            .idUsers(idUser)
                            .nrMatriculation(studentsCreationDTO.getNrMatriculation())
                            .yearOfStudy(studentsCreationDTO.getYearOfStudy())
                            .semester(studentsCreationDTO.getSemester())
                            .groupOfStudy(studentsCreationDTO.getGroupOfStudy())
                            .enrollmentDate(studentsCreationDTO.getEnrollmentDate())
                            .build();

            studentsRepository.save(students);
        }catch (Exception e){
            System.out.printf("Error: %s", e.getMessage());
        }
    }
}
