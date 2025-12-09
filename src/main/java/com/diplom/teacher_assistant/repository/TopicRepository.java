package com.diplom.teacher_assistant.repository;

import com.diplom.teacher_assistant.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
