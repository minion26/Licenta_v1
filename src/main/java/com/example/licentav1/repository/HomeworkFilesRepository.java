package com.example.licentav1.repository;

import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.HomeworkFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HomeworkFilesRepository extends JpaRepository<HomeworkFiles, UUID> {
    List<HomeworkFiles> findAllByHomework(Homework homework);

    @Query(value = "SELECT * FROM homework_files WHERE id_homework = :idHomework", nativeQuery = true)
    Optional<HomeworkFiles> findByIdHomework(@PathVariable("idHomework") UUID idHomework);
}
