CREATE TABLE courses(
                        id_courses UUID PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        year INT NOT NULL,
                        semester INT NOT NULL,
                        credits INT NOT NULL,
                        description VARCHAR(100) NOT NULL,
                        FOREIGN KEY (id_courses) REFERENCES courses(id_courses)
);

CREATE TABLE didactic(
                         id_didactic UUID PRIMARY KEY,
                         id_teacher UUID NOT NULL,
                         id_courses UUID NOT NULL,
                         FOREIGN KEY (id_teacher) REFERENCES teachers(id_users),
                         FOREIGN KEY (id_courses) REFERENCES courses(id_courses)
);