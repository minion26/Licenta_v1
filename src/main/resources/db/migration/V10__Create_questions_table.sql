CREATE TABLE questions (
                           id_question UUID PRIMARY KEY,
                           question_text TEXT NOT NULL,
                           id_exam UUID NOT NULL,
                           FOREIGN KEY (id_exam) REFERENCES exam(id_exam)
);