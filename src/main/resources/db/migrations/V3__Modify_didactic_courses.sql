-- Drop the existing foreign key constraint
ALTER TABLE courses
    DROP CONSTRAINT courses_id_courses_fkey;

-- Add the new foreign key constraint
-- ALTER TABLE courses
--     ADD CONSTRAINT courses_id_courses_fkey FOREIGN KEY (id_courses) REFERENCES didactic(id_courses);