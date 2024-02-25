CREATE TABLE students_follow_courses (
    id_students_follow_course UUID PRIMARY KEY ,
    id_student UUID NOT NULL,
    id_course UUID NOT NULL,
    FOREIGN KEY (id_student) REFERENCES students(id_users),
    FOREIGN KEY (id_course) REFERENCES courses(id_courses)
);