package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByEmail(String email);

    Tutor findByTutorId(Long tutorId);

    boolean existsByEmail(String email);

    long countByIsActive(Boolean isActive);


    List<Tutor> findAllByOrderByFullName();

    List<Tutor> findTop10ByOrderByCreatedAtDesc();

    List<Tutor> findByIsActiveTrue();

    List<Tutor> findByIsActiveFalse();

    List<Tutor> findByRolesContaining(String admin);
}