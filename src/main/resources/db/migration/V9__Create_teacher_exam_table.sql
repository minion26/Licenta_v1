CREATE TABLE teacher_exam (
                              id_teacher_exam UUID PRIMARY KEY,
                              id_teacher UUID NOT NULL,
                              id_exam UUID NOT NULL,
                              FOREIGN KEY (id_teacher) REFERENCES teachers(id_users),
                              FOREIGN KEY (id_exam) REFERENCES exam(id_exam)
);