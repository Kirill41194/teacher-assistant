package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // Найти предмет по названию
    Optional<Subject> findByName(String name);

    // Поиск по части названия
    List<Subject> findByNameContainingIgnoreCase(String name);

    // Получить все предметы с количеством студентов
    @Query("SELECT s, COUNT(ss.student) as studentCount " +
            "FROM Subject s LEFT JOIN StudentSubject ss ON s.subjectId = ss.subject.subjectId " +
            "GROUP BY s.subjectId " +
            "ORDER BY studentCount DESC")
    List<Object[]> findAllWithStudentCount();
}