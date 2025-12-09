package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
