package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    // Найти по студенту и предмету
    Optional<StudentSubject> findByStudent_StudentIdAndSubject_SubjectId(Long studentId, Long subjectId);

    // Все предметы студента
    List<StudentSubject> findByStudent_StudentId(Long studentId);

    // Все студенты по предмету
    List<StudentSubject> findBySubject_SubjectId(Long subjectId);

    // Студенты с низким прогрессом по предмету
    @Query("SELECT ss FROM StudentSubject ss WHERE ss.subject.subjectId = :subjectId AND ss.progressLevel < :minProgress")
    List<StudentSubject> findStudentsWithLowProgress(@Param("subjectId") Long subjectId,
                                                     @Param("minProgress") Integer minProgress);

    // Средний прогресс по предмету
    @Query("SELECT AVG(ss.progressLevel) FROM StudentSubject ss WHERE ss.subject.subjectId = :subjectId")
    Double getAverageProgressBySubject(@Param("subjectId") Long subjectId);
}