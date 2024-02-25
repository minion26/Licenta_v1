package com.example.licentav1.repository;

import com.example.licentav1.domain.StudentsFollowCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface StudentsFollowCoursesRepository extends JpaRepository<StudentsFollowCourses, UUID> {
}
