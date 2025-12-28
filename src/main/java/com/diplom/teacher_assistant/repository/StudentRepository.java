package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Student;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByTutor_TutorIdOrderByFullName(Long tutorId);

    boolean existsByEmailAndTutor_TutorId(String email, Long tutorId);

    boolean existsByTutor_TutorId(Long tutorId);

    @Query("SELECT s FROM Student s " +
            "WHERE s.tutor.tutorId = :tutorId " +
            "AND s.studentId NOT IN (" +
            "    SELECT ss.student.studentId FROM StudentSubject ss " +
            "    WHERE ss.subject.subjectId = :subjectId" +
            ") " +
            "ORDER BY s.fullName")
    List<Student> findStudentsNotEnrolledInSubject(
            @Param("subjectId") Long subjectId,
            @Param("tutorId") Long tutorId
    );

    Optional<Student> findByStudentIdAndTutor_TutorId(Long studentId, Long tutorId);
}
