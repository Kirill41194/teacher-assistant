package com.diplom.teacher_assistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    @Email
    private String email;

    private String telegram;

    @Column(nullable = false)
    private Integer age;

    private String level;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;
}