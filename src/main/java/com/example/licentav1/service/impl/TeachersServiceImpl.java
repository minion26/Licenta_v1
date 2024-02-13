package com.example.licentav1.service.impl;

import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.mapper.UsersMapper;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.TeachersService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TeachersServiceImpl implements TeachersService {

    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;

    public TeachersServiceImpl(TeachersRepository teachersRepository, UsersRepository usersRepository) {
        this.teachersRepository = teachersRepository;
        this.usersRepository = usersRepository;
    }


    @Override
    public Iterable<Teachers> getTeachers() {
        return teachersRepository.findAll();
    }

    @Override
    public void createTeacher(TeachersCreationDTO teachersCreationDTO) {
        Users users;
        UUID idUser = null;

        try{
            users = UsersMapper.fromTeacherCreationDTO(teachersCreationDTO);
            usersRepository.save(users);
            idUser = users.getIdUsers();
        } catch (Exception e) {
            System.out.printf("Error: %s", e.getMessage());
        }

        try{
            Teachers teachers = Teachers.builder()
                    .idUsers(idUser)
                    .idTeacher(teachersCreationDTO.getIdTeacher())
                    .degree(teachersCreationDTO.getDegree())
                    .build();

            teachersRepository.save(teachers);
        }catch (Exception e){
            System.out.printf("Error: %s", e.getMessage());
        }
    }
}
