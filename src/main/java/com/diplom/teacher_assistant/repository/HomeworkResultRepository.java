package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.HomeworkResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HomeworkResultRepository extends JpaRepository<HomeworkResult, Long> {

    // Результаты студента
    List<HomeworkResult> findByStudent_StudentId(Long studentId);

    // Результаты по теме
    List<HomeworkResult> findByTopic_TopicId(Long topicId);

    // Результаты за период
    List<HomeworkResult> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Средняя оценка студента
    @Query("SELECT AVG(hr.score) FROM HomeworkResult hr WHERE hr.student.studentId = :studentId")
    Double getAverageScoreByStudent(@Param("studentId") Long studentId);

    // Найти лучшие результаты
    @Query("SELECT hr FROM HomeworkResult hr WHERE hr.score > :minScore ORDER BY hr.score DESC")
    List<HomeworkResult> findTopResults(@Param("minScore") Integer minScore);
}