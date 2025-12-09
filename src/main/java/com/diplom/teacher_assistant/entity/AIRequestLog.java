package com.diplom.teacher_assistant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(columnDefinition = "TEXT")
    private String requestText;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    private Integer tokensUsed;

    private String model; // gpt-4o, gpt-5, llama3...

    private LocalDateTime createdAt = LocalDateTime.now();
}
