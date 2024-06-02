ALTER TABLE feedback
    RENAME COLUMN idNote TO id_note;

ALTER TABLE feedback
    ALTER COLUMN id_note TYPE UUID USING id_note::uuid;