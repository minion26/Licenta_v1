package com.example.licentav1.service.impl;

import com.example.licentav1.advice.exceptions.CourseNotFoundException;
import com.example.licentav1.advice.exceptions.ExamNotFoundException;
import com.example.licentav1.advice.exceptions.TeacherNotFoundException;
import com.example.licentav1.domain.*;
import com.example.licentav1.dto.*;
import com.example.licentav1.mapper.ExamMapper;
import com.example.licentav1.mapper.QuestionsExamMapper;
import com.example.licentav1.mapper.TeacherExamMapper;
import com.example.licentav1.repository.*;
import com.example.licentav1.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ExamServiceImpl implements ExamService {
    private ExamRepository examRepository;
    private CoursesRepository coursesRepository;
    private QuestionRepository questionRepository;
    private QuestionsExamRepository questionsExamRepository;
    private TeacherExamRepository teacherExamRepository;
    private TeachersRepository teachersRepository;
    private UsersRepository usersRepository;

    public ExamServiceImpl(ExamRepository examRepository, CoursesRepository coursesRepository,QuestionRepository questionRepository, QuestionsExamRepository questionsExamRepository, TeacherExamRepository teacherExamRepository, TeachersRepository teachersRepository, UsersRepository usersRepository) {
        this.examRepository = examRepository;
        this.coursesRepository = coursesRepository;
        this.questionRepository = questionRepository;
        this.questionsExamRepository = questionsExamRepository;
        this.teacherExamRepository = teacherExamRepository;
        this.teachersRepository = teachersRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public void createExam(ExamCreationDTO examCreationDTO, UUID idCourse) {
        //gasesc cursul pentru a asocia examenul
        Courses courses = coursesRepository.findById(idCourse).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        //gasesc profesorii pentru a asocia examenul
        List<Teachers> teachers = new ArrayList<>();
        for (UUID id: examCreationDTO.getIdTeacher()) {
            Teachers t = teachersRepository.findById(id).orElseThrow(() -> new TeacherNotFoundException("Teacher not found"));
            if (t != null)
                teachers.add(t);
        }

        // salvez examenul: nume, timp, scor total, scor de trecere, data, curs
        Exam exam =  examRepository.save(ExamMapper.fromDTO(examCreationDTO, courses));

        // salvez intrebarile
        List<Question> questions = new ArrayList<>();
        for (QuestionCreationDTO questionDTO : examCreationDTO.getQuestion()) {
            // salvez intrebarea
            Question question = new Question();
            if(questionDTO.getIdQuestion() != null) // e null cand creez examenul si nu am id ca el e generat automat in questions entity
                question.setIdQuestion(questionDTO.getIdQuestion());

            question.setQuestionText(questionDTO.getQuestionText());
            question.setExam(exam);
            questions.add(question);
            //o pun in tabel cu examenul
            questionRepository.save(question);

        }

        exam.setQuestionsList(questions);
        examRepository.save(exam);

        //salvez relatia intre examen si cati profesori sunt
        for(Teachers t: teachers){
            teacherExamRepository.save(TeacherExamMapper.fromDTO(exam, t));
        }

        //salvez relatia intre intrebari si examen
        for(Question q: questions){
            questionsExamRepository.save(QuestionsExamMapper.fromDTO(exam, q));
        }


    }

    @Override
    public List<ExamDTO> getAllExams() {
        return examRepository.findAll().stream().map(
                exam -> {
                    ExamDTO examDTO = ExamMapper.toDTO(exam);

                    //gasesc intrebarile pentru examen
                    List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
                    List<QuestionDTO> questions = new ArrayList<>();
                    for(QuestionsExam qe: questionsExams){
                        QuestionDTO questionDTO = new QuestionDTO();
                        questionDTO.setIdQuestion(qe.getQuestion().getIdQuestion());
                        questionDTO.setQuestionText(qe.getQuestion().getQuestionText());
                        questionDTO.setIdExam(qe.getExam().getIdExam());
                        questions.add(questionDTO);
                    }
                    examDTO.setQuestions(questions);

                    //gasesc profesorii pentru examen
                    List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
                    List<UUID> teacherIds = new ArrayList<>();
                    for(TeacherExam te: teacherExams){
                        teacherIds.add(te.getTeacher().getIdUsers());
                    }
                    examDTO.setIdTeachers(teacherIds);


                    return examDTO;
                }

        ).collect(Collectors.toList());

    }

    @Override
    public void deleteExam(UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        List<QuestionsExam> questionsExams = questionsExamRepository.findAllByIdExam(exam.getIdExam());
        questionsExamRepository.deleteAll(questionsExams);

        List<TeacherExam> teacherExams = teacherExamRepository.findAllByIdExam(exam.getIdExam());
        teacherExamRepository.deleteAll(teacherExams);

        List<Question> questions = questionRepository.findAllByIdExam(exam.getIdExam());
        questionRepository.deleteAll(questions);

        examRepository.delete(exam);
    }

    @Override
    public void updateExam(ExamCreationDTO examCreationDTO, UUID idExam) {
        Exam exam = examRepository.findById(idExam).orElseThrow(() -> new ExamNotFoundException("Exam not found"));

        if(examCreationDTO.getName() != null)
            exam.setName(examCreationDTO.getName());
        if(examCreationDTO.getTotalScore() != null)
            exam.setTotalScore(examCreationDTO.getTotalScore());
        if(examCreationDTO.getPassingScore() != null)
            exam.setPassingScore(examCreationDTO.getPassingScore());
        if(examCreationDTO.getDate() != null)
            exam.setDate(examCreationDTO.getDate());
        if(examCreationDTO.getTimeInMinutes() != null)
            exam.setTimeInMinutes(examCreationDTO.getTimeInMinutes());

        List<Question> questions = questionRepository.findAllByIdExam(exam.getIdExam());
        for(Question q: questions){

            for (QuestionCreationDTO questionDTO : examCreationDTO.getQuestion()) {

                if(q.getIdQuestion().equals(questionDTO.getIdQuestion())){

                    if(questionDTO.getQuestionText() != null)
                        q.setQuestionText(questionDTO.getQuestionText());
                    questionRepository.save(q);
                }
            }
        }

        examRepository.save(exam);

    }
}
