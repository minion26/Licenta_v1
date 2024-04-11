CREATE TABLE homework_announcements (
                                        id_homework_announcements UUID PRIMARY KEY,
                                        id_lecture UUID REFERENCES lectures(id_lecture),
                                        title VARCHAR(255),
                                        description TEXT,
                                        due_date DATE,
                                        score INT
);

ALTER TABLE homework
    DROP COLUMN title,
    DROP COLUMN description,
    DROP COLUMN id_lecture;

ALTER TABLE homework
    ADD COLUMN id_homework_announcement UUID REFERENCES homework_announcements(id_homework_announcements);