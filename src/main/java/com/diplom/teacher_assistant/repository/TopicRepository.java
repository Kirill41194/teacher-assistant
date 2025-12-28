package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findBySubject_SubjectIdOrderByName(Long subjectId);

    boolean existsByNameAndSubject_SubjectId(String name, Long subjectId);

    boolean existsBySubject_SubjectId(Long subjectId);

    long countBySubject_SubjectId(Long subjectId);

    Optional<Topic> findByTopicIdAndSubject_Tutor_TutorId(Long topicId, Long tutorId);

    boolean existsByTopicIdAndSubject_Tutor_TutorId(Long topicId, Long tutorId);
}
