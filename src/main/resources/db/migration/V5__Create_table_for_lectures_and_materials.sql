CREATE TABLE lectures (
    id_lecture UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    week INT NOT NULL,
    semester INT NOT NULL,
    year INT NOT NULL,
    id_course UUID NOT NULL,
    FOREIGN KEY (id_course) REFERENCES courses(id_courses)
);

CREATE TABLE materials(
    id_material UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    id_lecture UUID NOT NULL,
    FOREIGN KEY (id_lecture) REFERENCES lectures(id_lecture)
)