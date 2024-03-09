ALTER TABLE questions_exam
    ALTER COLUMN question TYPE UUID USING question::UUID;