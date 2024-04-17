CREATE TABLE homework_files (
                                id_homework_files UUID PRIMARY KEY,
                                id_homework UUID,
                                file_url TEXT,
                                FOREIGN KEY (id_homework) REFERENCES homework(id_homework)
);