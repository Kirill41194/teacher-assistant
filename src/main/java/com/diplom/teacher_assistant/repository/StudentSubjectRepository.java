package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.entity.StudentSubject;
import com.diplom.teacher_assistant.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {

    // 1. Основные методы поиска
    Optional<StudentSubject> findByStudent_StudentIdAndSubject_SubjectId(Long studentId, Long subjectId);

    List<StudentSubject> findByStudent_StudentId(Long studentId);

    List<StudentSubject> findBySubject_SubjectId(Long subjectId);

    boolean existsByStudentAndSubject(Student student, Subject subject);

    boolean existsByStudent_StudentIdAndSubject_SubjectId(Long studentId, Long subjectId);

    // 2. Методы с проверкой преподавателя (безопасность)
    @Query("SELECT ss FROM StudentSubject ss " +
            "WHERE ss.student.studentId = :studentId " +
            "AND ss.student.tutor.tutorId = :tutorId")
    List<StudentSubject> findByStudentIdAndTutorId(
            @Param("studentId") Long studentId,
            @Param("tutorId") Long tutorId
    );

    @Query("SELECT ss FROM StudentSubject ss " +
            "WHERE ss.subject.subjectId = :subjectId " +
            "AND ss.subject.tutor.tutorId = :tutorId")
    List<StudentSubject> findBySubjectIdAndTutorId(
            @Param("subjectId") Long subjectId,
            @Param("tutorId") Long tutorId
    );

    @Query("SELECT ss FROM StudentSubject ss " +
            "WHERE ss.student.studentId = :studentId " +
            "AND ss.subject.subjectId = :subjectId " +
            "AND ss.student.tutor.tutorId = :tutorId")
    Optional<StudentSubject> findByStudentIdAndSubjectIdAndTutorId(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId,
            @Param("tutorId") Long tutorId
    );

    @Query("SELECT AVG(ss.progressLevel) FROM StudentSubject ss " +
            "WHERE ss.student.studentId = :studentId " +
            "AND ss.student.tutor.tutorId = :tutorId")
    Double findAverageProgressByStudentIdAndTutorId(
            @Param("studentId") Long studentId,
            @Param("tutorId") Long tutorId
    );

    @Query("SELECT COUNT(ss) FROM StudentSubject ss " +
            "WHERE ss.student.studentId = :studentId " +
            "AND ss.student.tutor.tutorId = :tutorId")
    Long countByStudentIdAndTutorId(
            @Param("studentId") Long studentId,
            @Param("tutorId") Long tutorId
    );

    @Query("DELETE FROM StudentSubject ss " +
            "WHERE ss.student.studentId = :studentId " +
            "AND ss.subject.subjectId = :subjectId " +
            "AND ss.student.tutor.tutorId = :tutorId")
    void deleteByStudentIdAndSubjectIdAndTutorId(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId,
            @Param("tutorId") Long tutorId
    );

    List<StudentSubject> findByStudent_StudentIdAndProgressLevelGreaterThanEqual(
            Long studentId, Integer minProgress);

    List<StudentSubject> findByStudent_StudentIdAndProgressLevelLessThanEqual(
            Long studentId, Integer maxProgress);


    void deleteByStudent_StudentId(Long studentId);

    void deleteBySubject_SubjectId(Long subjectId);
}