ALTER TABLE student_homework
    ADD COLUMN id_homework_announcement UUID,
    ADD CONSTRAINT fk_homework_announcement
        FOREIGN KEY (id_homework_announcement) REFERENCES homework_announcements(id_homework_announcements);