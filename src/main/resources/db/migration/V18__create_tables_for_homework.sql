CREATE TABLE homework (
                          id_homework UUID PRIMARY KEY,
                          title TEXT NOT NULL,
                          description TEXT NOT NULL,
                          grade INTEGER NOT NULL,
                          dueDate DATE NOT NULL,
                          file_url TEXT NOT NULL,
                          id_lecture UUID NOT NULL,
                          FOREIGN KEY (id_lecture) REFERENCES lectures(id_lecture)
);

CREATE TABLE feedback (
                          id_feedback UUID PRIMARY KEY,
                          content TEXT NOT NULL,
                          id_homework UUID NOT NULL,
                          FOREIGN KEY (id_homework) REFERENCES homework(id_homework)
);

CREATE TABLE student_homework (
                                  id_student_homework UUID PRIMARY KEY,
                                  id_student UUID NOT NULL,
                                  id_homework UUID NOT NULL,
                                  FOREIGN KEY (id_student) REFERENCES students(id_users),
                                  FOREIGN KEY (id_homework) REFERENCES homework(id_homework)
);