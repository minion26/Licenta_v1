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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class TeachersServiceImpl implements TeachersService {

    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public TeachersServiceImpl(TeachersRepository teachersRepository, UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.teachersRepository = teachersRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
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
    public TeachersDTO getTeacher(UUID id) {
        Teachers teahcers = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return TeachersMapper.toDTO(users, teahcers);
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

            String password = UUID.randomUUID().toString().substring(0, 8);
            System.out.println(password);

            users.setPassword(passwordEncoder.encode(password));
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
        if (teachersDTO.getFirstName() != null) {
            users.setFirstName(teachersDTO.getFirstName());
        }
        if (teachersDTO.getLastName() != null) {
            users.setLastName(teachersDTO.getLastName());
        }
        if (teachersDTO.getPersonalEmail() != null) {
            users.setPersonalEmail(teachersDTO.getPersonalEmail());
        }
        usersRepository.save(users);

        Teachers teacher = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));

        if(teachersDTO.getDegree() != null) {
            teacher.setDegree(teachersDTO.getDegree());
        }
        teachersRepository.save(teacher);
    }

    @Override
    public void deleteTeacher(UUID id)throws UserNotFoundException, TeacherNotFoundException{
        Teachers teacher = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
        teachersRepository.delete(teacher);

        Users user = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        usersRepository.delete(user);
    }

    @Override
    public void uploadTeachers(MultipartFile file) throws IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String password = UUID.randomUUID().toString().substring(0, 8); // trebuie trimisa si pe email cand se creeaza contul se trimite
            // email-ul si parola
            Users users = UsersMapper.fromCsvDataTeacher(data);
            users.setPassword(passwordEncoder.encode(password));

            if (usersRepository.existsByFacultyEmail(users.getFacultyEmail())) {
                throw new UserAlreadyExistsException("Same email    already exists");
            }
            usersRepository.save(users);
            UUID idUser = users.getIdUsers();

            Teachers teachers = TeachersMapper.fromCsvData(data, idUser);

            if (teachersRepository.existsByIdTeacher(teachers.getIdTeacher())) {
                throw new TeacherAlreadyExistsException("Teacher already exists");
            }

            teachersRepository.save(teachers);
        }
    }


}
