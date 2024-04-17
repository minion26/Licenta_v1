package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.DidacticRelationNotFoundException;
import com.example.licentav1.advice.exceptions.LectureNotFoundException;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.HomeworkAnnouncementsCreationDTO;
import com.example.licentav1.dto.HomeworkAnnouncementsDTO;
import com.example.licentav1.mapper.HomeworkAnnouncementsMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.HomeworkAnnouncementsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class HomeworkAnnouncementsServiceImpl implements HomeworkAnnouncementsService {
    private final HomeworkAnnouncementsRepository homeworkAnnouncementsRepository;
    private final LecturesRepository lectureRepository;
    private final CoursesRepository coursesRepository;
    private final DidacticRepository didacticRepository;
    private final TeachersRepository teachersRepository;

    public HomeworkAnnouncementsServiceImpl(HomeworkAnnouncementsRepository homeworkAnnouncementsRepository, LecturesRepository lectureRepository, CoursesRepository coursesRepository, DidacticRepository didacticRepository, TeachersRepository teachersRepository) {
        this.homeworkAnnouncementsRepository = homeworkAnnouncementsRepository;
        this.lectureRepository = lectureRepository;
        this.coursesRepository = coursesRepository;
        this.didacticRepository = didacticRepository;
        this.teachersRepository = teachersRepository;
    }

    @Override
    public void createHomeworkAnnouncement(UUID idLecture, HomeworkAnnouncementsCreationDTO homeworkAnnouncementsCreationDTO) {
        //get the lecture
        Lectures lecture = lectureRepository.findById(idLecture).orElseThrow(() -> new LectureNotFoundException("Lecture not found!"));

        //get the course
        Courses course = coursesRepository.findById(lecture.getCourses().getIdCourses()).orElseThrow(() -> new CourseNotFoundException("Course not found!"));

        Didactic didactic = didacticRepository.findByIdCourses(course.getIdCourses()).orElseThrow(() -> new DidacticRelationNotFoundException("Didactic relation not found!"));

        Teachers teacher = teachersRepository.findById(didactic.getTeachers().getIdUsers()).orElseThrow(() -> new DidacticRelationNotFoundException("Teacher not found!"));

        //create the homework announcement
        HomeworkAnnouncements homeworkAnnouncements = HomeworkAnnouncementsMapper.fromDTO(homeworkAnnouncementsCreationDTO, lecture);

        //save the homework announcement
        homeworkAnnouncementsRepository.save(homeworkAnnouncements);
    }

    @Override
    public List<HomeworkAnnouncementsDTO> getHomeworkAnnouncements(UUID idLecture) {
        //get the lecture
        Lectures lecture = lectureRepository.findById(idLecture).orElseThrow(() -> new LectureNotFoundException("Lecture not found!"));

        //get all the homework announcements for the lecture
        List<HomeworkAnnouncements> homeworkAnnouncements = homeworkAnnouncementsRepository.findAllByLectures(lecture);

        List<HomeworkAnnouncementsDTO> homeworkAnnouncementsDTO = new ArrayList<>();

        for (HomeworkAnnouncements homeworkAnnouncement : homeworkAnnouncements) {
            homeworkAnnouncementsDTO.add(HomeworkAnnouncementsMapper.toDTO(homeworkAnnouncement));
        }

        return homeworkAnnouncementsDTO;
    }

    @Override
    public void deleteHomeworkAnnouncement(UUID idHomeworkAnnouncement) {
        homeworkAnnouncementsRepository.deleteById(idHomeworkAnnouncement);
    }

    @Override
    public void updateHomeworkAnnouncement(UUID idHomeworkAnnouncement, HomeworkAnnouncementsDTO homeworkAnnouncementsDTO) {
        //get the homework announcement
        HomeworkAnnouncements homeworkAnnouncement = homeworkAnnouncementsRepository.findById(idHomeworkAnnouncement).orElseThrow(() -> new LectureNotFoundException("Homework announcement not found!"));

        //update the homework announcement
        if (homeworkAnnouncementsDTO.getTitle() != null) {
            homeworkAnnouncement.setTitle(homeworkAnnouncementsDTO.getTitle());
        }
        if (homeworkAnnouncementsDTO.getDescription() != null) {
            homeworkAnnouncement.setDescription(homeworkAnnouncementsDTO.getDescription());
        }
        if (homeworkAnnouncementsDTO.getDueDate() != null) {
            homeworkAnnouncement.setDueDate(homeworkAnnouncementsDTO.getDueDate());
        }
        if (homeworkAnnouncementsDTO.getScore() != null) {
            homeworkAnnouncement.setScore(homeworkAnnouncementsDTO.getScore());
        }


        //save the homework announcement
        homeworkAnnouncementsRepository.save(homeworkAnnouncement);
    }
}
