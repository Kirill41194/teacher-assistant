package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {


    List<Subject> findByTutor_TutorIdOrderByName(Long tutorId);

    boolean existsByNameAndTutor_TutorId(String name, Long tutorId);


    @Query("SELECT s FROM Subject s " +
            "WHERE s.tutor.tutorId = :tutorId " +
            "AND s.subjectId NOT IN (" +
            "    SELECT ss.subject.subjectId FROM StudentSubject ss " +
            "    WHERE ss.student.studentId = :studentId" +
            ") " +
            "ORDER BY s.name")
    List<Subject> findSubjectsNotEnrolledByStudent(
            @Param("studentId") Long studentId,
            @Param("tutorId") Long tutorId
    );

    Optional<Subject> findBySubjectIdAndTutor_TutorId(Long subjectId, Long tutorId);


}