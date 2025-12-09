package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // Уведомления студента
    List<NotificationLog> findByStudent_StudentId(Long studentId);

    // Уведомления по статусу
    List<NotificationLog> findByStatus(String status);

    // Уведомления по каналу
    List<NotificationLog> findByChannel(String channel);

    // Неудавшиеся уведомления за период
    @Query("SELECT nl FROM NotificationLog nl WHERE nl.status = 'FAILED' AND nl.createdAt BETWEEN :start AND :end")
    List<NotificationLog> findFailedNotifications(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // Статистика по каналам
    @Query("SELECT nl.channel, COUNT(nl) FROM NotificationLog nl GROUP BY nl.channel")
    List<Object[]> getNotificationStatsByChannel();
}