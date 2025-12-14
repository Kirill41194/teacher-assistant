package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {


    List<Subject> findByTutor_TutorIdOrderByName(Long tutorId);

    boolean existsByNameAndTutor_TutorId(String name, Long tutorId);

}