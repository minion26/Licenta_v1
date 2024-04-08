package com.example.licentav1.mapper;

import com.example.licentav1.domain.Exam;
import com.example.licentav1.domain.Question;
import com.example.licentav1.domain.QuestionsExam;
import com.example.licentav1.dto.ExamCreationDTO;
import com.example.licentav1.dto.QuestionsExamDTO;
import org.springframework.stereotype.Component;


@Component
public class QuestionsExamMapper {
    public static QuestionsExam fromDTO(Exam exam, Question question){
        return QuestionsExam.builder()
                .exam(exam)
                .question(question)
                .build();

    }

    public static QuestionsExamDTO toDTO(QuestionsExam questionsExam) {
        if (questionsExam == null) {
            throw new IllegalArgumentException("QuestionsExam must not be null");
        }
        if (questionsExam.getQuestion() == null) {
            throw new IllegalArgumentException("Question must not be null");
        }
        if (questionsExam.getExam() == null) {
            throw new IllegalArgumentException("Exam must not be null");
        }


        return QuestionsExamDTO.builder()
                .idQuestionsExam(questionsExam.getIdQuestionsExam())
                .questionId(questionsExam.getQuestion().getIdQuestion())
                .examId(questionsExam.getExam().getIdExam())
                .build();

    }
}
