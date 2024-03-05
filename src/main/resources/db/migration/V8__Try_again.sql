CREATE TABLE exam(
                     id_exam UUID PRIMARY KEY,
                     name VARCHAR(100) NOT NULL,
                     questions text NOT NULL,
                     time_in_minutes int NOT NULL,
                     total_score int NOT NULL,
                     passing_score int NOT NULL,
                     date DATE NOT NULL,
                     course_id UUID NOT NULL,
                     teacher_id UUID NOT NULL,
                     FOREIGN KEY (course_id) REFERENCES courses(id_courses),
                     FOREIGN KEY (teacher_id) REFERENCES teachers(id_users)
);

CREATE TABLE questions_exam(
                               id_question_exam UUID PRIMARY KEY,
                               question text NOT NULL,
                               id_exam UUID NOT NULL,
                               FOREIGN KEY (id_exam) REFERENCES exam(id_exam)
);

CREATE TABLE correct_answers_exam(
                                     id_answer_exam UUID PRIMARY KEY,
                                     correct_answer text NOT NULL,
                                     score int NOT NULL,
                                     id_question_exam UUID NOT NULL,
                                     FOREIGN KEY (id_question_exam) REFERENCES questions_exam(id_question_exam)
);

CREATE TABLE student_exam(
                             id_student_exam UUID PRIMARY KEY,
                             id_student UUID NOT NULL,
                             id_exam UUID NOT NULL,
                             score int NOT NULL,
                             FOREIGN KEY (id_student) REFERENCES students(id_users),
                             FOREIGN KEY (id_exam) REFERENCES exam(id_exam)
);

CREATE TABLE student_answers_exam(
                                     id_student_answer_exam UUID PRIMARY KEY,
                                     student_answer text NOT NULL,
                                     id_student_exam UUID NOT NULL,
                                     id_question_exam UUID NOT NULL,
                                     FOREIGN KEY (id_student_exam) REFERENCES student_exam(id_student_exam),
                                     FOREIGN KEY (id_question_exam) REFERENCES questions_exam(id_question_exam)
);


