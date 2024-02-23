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



    public StudentsServiceImpl(StudentsRepository studentsRepository, UsersRepository usersRepository) {
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
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
        students.setNrMatriculation(studentsDTO.getNrMatriculation());
        students.setYearOfStudy(studentsDTO.getYearOfStudy());
        students.setSemester(studentsDTO.getSemester());
        students.setGroupOfStudy(studentsDTO.getGroupOfStudy());
        studentsRepository.save(students);

        // Fetch and update the Users entity
        Users users = usersRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        users.setFirstName(studentsDTO.getFirstName());
        users.setLastName(studentsDTO.getLastName());
        users.setPersonalEmail(studentsDTO.getPersonalEmail());
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
            Users users = UsersMapper.fromCsvData(data);

            if (usersRepository.existsByFacultyEmail(users.getFacultyEmail())) {
                throw new UserAlreadyExistsException("User already exists");
            }
            usersRepository.save(users);
            UUID idUser = users.getIdUsers();

            Students students = Students.builder()
                    .idUsers(idUser)
                    .nrMatriculation(data[4])
                    .yearOfStudy(Integer.parseInt(data[5]))
                    .semester(Integer.parseInt(data[6]))
                    .groupOfStudy(data[7])
                    .enrollmentDate(java.time.LocalDateTime.now())
                    .build();
            if (studentsRepository.existsByNrMatriculation(students.getNrMatriculation())) {
                throw new StudentAlreadyExistsException("Student with this code already exists");
            }
            studentsRepository.save(students);
        }
    }
}
