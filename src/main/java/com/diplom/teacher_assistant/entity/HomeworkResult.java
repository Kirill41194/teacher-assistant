package com.diplom.teacher_assistant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private Integer score; // 0-100%

    @Column(columnDefinition = "TEXT")
    private String mistakes; // JSON или текст

    @Column(columnDefinition = "TEXT")
    private String aiAnalysis;

    private LocalDateTime createdAt = LocalDateTime.now();
}
