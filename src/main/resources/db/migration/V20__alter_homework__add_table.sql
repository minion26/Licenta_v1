ALTER TABLE homework
DROP COLUMN file_url;

ALTER TABLE homework
ADD COLUMN team_members TEXT Default NULL;

CREATE TABLE homework_files (
        id UUID PRIMARY KEY,
        id_homework UUID,
        file_url VARCHAR(255),
        FOREIGN KEY (id_homework) REFERENCES homework(id_homework)
);