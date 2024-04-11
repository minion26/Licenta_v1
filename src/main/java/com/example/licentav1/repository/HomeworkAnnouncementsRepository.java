package com.example.licentav1.repository;

import com.example.licentav1.domain.HomeworkAnnouncements;
import com.example.licentav1.domain.Lectures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HomeworkAnnouncementsRepository extends JpaRepository<HomeworkAnnouncements, UUID> {

    @Query("SELECT ha FROM HomeworkAnnouncements ha WHERE ha.lectures = :lecture")
    List<HomeworkAnnouncements> findAllByLectures(Lectures lecture);
}
