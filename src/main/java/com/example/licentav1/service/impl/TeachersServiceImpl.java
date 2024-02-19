package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.TeacherAlreadyExistsException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Teachers;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.TeachersCreationDTO;
import com.example.licentav1.dto.TeachersDTO;
import com.example.licentav1.mapper.UsersMapper;
import com.example.licentav1.mapper.TeachersMapper;
import com.example.licentav1.repository.TeachersRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.TeachersService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public Iterable<TeachersDTO> getTeachers() {
        ArrayList<Teachers> teachers = (ArrayList<Teachers>) teachersRepository.findAll();
        ArrayList<Users> users = (ArrayList<Users>) usersRepository.findAll();
        ArrayList<TeachersDTO> teachersDTO = new ArrayList<>();

        for (Teachers teacher : teachers) {
            for (Users user : users) {
                if (teacher.getIdUsers().equals(user.getIdUsers())) {
                    teachersDTO.add(TeachersMapper.toDTO(user, teacher));
                }
            }
        }
        return teachersDTO;
    }

    @Override
    public void createTeacher(TeachersCreationDTO teachersCreationDTO) throws UserAlreadyExistsException, TeacherAlreadyExistsException {
        if (usersRepository.existsByFacultyEmail(teachersCreationDTO.getFacultyEmail())) {
            throw new UserAlreadyExistsException("User's faculty email already exists");
        }
        if (usersRepository.existsByPersonalEmail(teachersCreationDTO.getPersonalEmail())) {
            throw new UserAlreadyExistsException("User's personal email already exists");
        }
        if (teachersRepository.existsByIdTeacher(teachersCreationDTO.getIdTeacher())) {
            throw new TeacherAlreadyExistsException("Teacher with this code already exists");
        }

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

    @Override
    public void updateTeacher(UUID id, TeachersDTO teachersDTO) throws UserNotFoundException, TeacherNotFoundException {
        // fetch the user and teacher from the database
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        //update the user
        users.setFirstName(teachersDTO.getFirstName());
        users.setLastName(teachersDTO.getLastName());
        users.setPersonalEmail(teachersDTO.getPersonalEmail());
        usersRepository.save(users);

        Teachers teacher = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        teacher.setDegree(teachersDTO.getDegree());
        teachersRepository.save(teacher);
    }

    @Override
    public void deleteTeacher(UUID id)throws UserNotFoundException, TeacherNotFoundException{
        Teachers teacher = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        teachersRepository.delete(teacher);

        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        usersRepository.delete(user);
    }
}
