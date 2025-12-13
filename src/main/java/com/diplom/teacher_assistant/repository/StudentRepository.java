package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Student;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByTutor_TutorIdOrderByFullName(Long tutorId);

    boolean existsByEmailAndTutor_TutorId(String email, Long tutorId);
}
