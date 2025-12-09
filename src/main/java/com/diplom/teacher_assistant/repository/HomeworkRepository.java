package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    // Домашние задания студента
    List<Homework> findByStudent_StudentId(Long studentId);

    // Просроченные домашние задания
    List<Homework> findByDeadlineBeforeAndStudent_StudentId(LocalDate date, Long studentId);

    // Предстоящие домашние задания
    @Query("SELECT h FROM Homework h WHERE h.deadline BETWEEN :startDate AND :endDate AND h.student.studentId = :studentId")
    List<Homework> findUpcomingHomeworks(@Param("studentId") Long studentId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    // Домашние задания по сложности
    List<Homework> findByDifficultyBetweenAndStudent_StudentId(Integer minDifficulty,
                                                               Integer maxDifficulty,
                                                               Long studentId);
}