package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.AIRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIRequestLogRepository extends JpaRepository<AIRequestLog, Long> {

    // Найти все логи по репетитору
    List<AIRequestLog> findByTutor_TutorId(Long tutorId);

    // Найти все логи по студенту
    List<AIRequestLog> findByStudent_StudentId(Long studentId);

    // Найти логи за определенный период
    @Query("SELECT l FROM AIRequestLog l WHERE l.createdAt BETWEEN :startDate AND :endDate")
    List<AIRequestLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Статистика по использованию токенов
    @Query("SELECT SUM(l.tokensUsed) FROM AIRequestLog l WHERE l.tutor.tutorId = :tutorId")
    Integer getTotalTokensUsedByTutor(@Param("tutorId") Long tutorId);
}