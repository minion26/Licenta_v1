package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.StudentAlreadyExistsException;
import com.example.licentav1.advice.exceptions.StudentNotFoundException;
import com.example.licentav1.advice.exceptions.UserAlreadyExistsException;
import com.example.licentav1.advice.exceptions.UserNotFoundException;
import com.example.licentav1.domain.Students;
import com.example.licentav1.domain.Users;
import com.example.licentav1.dto.StudentsCreationDTO;
import com.example.licentav1.dto.StudentsDTO;
import com.example.licentav1.dto.UsersDTO;
import com.example.licentav1.mapper.UsersMapper;
import com.example.licentav1.mapper.StudentsMapper;
import com.example.licentav1.repository.StudentsRepository;
import com.example.licentav1.repository.UsersRepository;
import com.example.licentav1.service.StudentsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class StudentsServiceImpl implements StudentsService {
    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;


    public StudentsServiceImpl(StudentsRepository studentsRepository, UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Iterable<StudentsDTO> getStudents() {
        // find all the students in the database and add the users information to the students
        ArrayList<Students> students = (ArrayList<Students>) studentsRepository.findAll();
        ArrayList< Users> users = (ArrayList<Users>) usersRepository.findAll();
        ArrayList<StudentsDTO> studentsDTO = new ArrayList<>();

        for (Students student : students) {
            for (Users user : users) {
                if (student.getIdUsers().equals(user.getIdUsers())) {
                    studentsDTO.add(StudentsMapper.toDTO(user, student));
                }
            }
        }

        return studentsDTO;
    }

    @Override
    public void createStudent(StudentsCreationDTO studentsCreationDTO) throws StudentAlreadyExistsException, UserAlreadyExistsException {
        if (usersRepository.existsByFacultyEmail(studentsCreationDTO.getFacultyEmail())) {
            throw new UserAlreadyExistsException("User's faculty email already exists");
        }
        if (usersRepository.existsByPersonalEmail(studentsCreationDTO.getPersonalEmail())) {
            throw new UserAlreadyExistsException("User's personal email already exists");
        }
        if (studentsRepository.existsByNrMatriculation(studentsCreationDTO.getNrMatriculation())) {
            throw new StudentAlreadyExistsException("Student with this code already exists");
        }

        Users users;
        UUID idUser = null;

        try{
            users = UsersMapper.fromStudentCreationDTO(studentsCreationDTO);
            users.setPassword(passwordEncoder.encode(studentsCreationDTO.getPassword()));
            if (usersRepository.existsByFacultyEmail(users.getFacultyEmail())) {
                throw new StudentAlreadyExistsException("User already exists");
            }
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

    @Override
    public void updateStudent(UUID id, StudentsDTO studentsDTO) throws UserNotFoundException, StudentNotFoundException {
        // Fetch and update the Students entity
        Students students = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("Student not found"));

        if (studentsDTO.getNrMatriculation() != null) {
            students.setNrMatriculation(studentsDTO.getNrMatriculation());
        }
        if (studentsDTO.getYearOfStudy() != null) {
            students.setYearOfStudy(studentsDTO.getYearOfStudy());
        }
        if (studentsDTO.getSemester() != null) {
            students.setSemester(studentsDTO.getSemester());
        }
        if (studentsDTO.getGroupOfStudy() != null) {
            students.setGroupOfStudy(studentsDTO.getGroupOfStudy());
        }
        studentsRepository.save(students);

        // Fetch and update the Users entity
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (studentsDTO.getFirstName() != null) {
            users.setFirstName(studentsDTO.getFirstName());
        }
        if (studentsDTO.getLastName() != null) {
            users.setLastName(studentsDTO.getLastName());
        }
        if (studentsDTO.getPersonalEmail() != null) {
            users.setPersonalEmail(studentsDTO.getPersonalEmail());
        }
        usersRepository.save(users);
    }

    @Override
    public void deleteStudent(UUID id) throws UserNotFoundException, StudentNotFoundException {
        // delete from students database
        Students students = studentsRepository.findById(id).orElseThrow(() -> new StudentNotFoundException("User not found"));
        studentsRepository.delete(students);

        // delete from users database
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        usersRepository.delete(users);

    }

    @Override
    public void uploadStudents(MultipartFile file) throws StudentAlreadyExistsException, UserAlreadyExistsException, IOException {
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",");
            String password = UUID.randomUUID().toString().substring(0, 8); // trebuie trimisa si pe email cand se creeaza contul se trimite
            //si pe email parola asta

            Users users = UsersMapper.fromCsvDataStudent(data);
            users.setPassword(passwordEncoder.encode(password)); // encode the password

            if (usersRepository.existsByFacultyEmail(users.getFacultyEmail())) {
                throw new UserAlreadyExistsException("User already exists");
            }
            usersRepository.save(users);
            UUID idUser = users.getIdUsers();

            Students students = StudentsMapper.fromCsvData(data, idUser);
            if (studentsRepository.existsByNrMatriculation(students.getNrMatriculation())) {
                throw new StudentAlreadyExistsException("Student with this code already exists");
            }
            studentsRepository.save(students);
        }
    }
}
