package com.example.licentav1.repository;

import com.example.licentav1.domain.Homework;
import com.example.licentav1.domain.HomeworkFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface HomeworkFilesRepository extends JpaRepository<HomeworkFiles, UUID> {
    List<HomeworkFiles> findAllByHomework(Homework homework);

}
