package com.diplom.teacher_assistant.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;  // Связь вместо строки

    @Column(nullable = false)
    private String name;     // Например: "Квадратные уравнения"

    @Column(columnDefinition = "TEXT")
    private String description;
}