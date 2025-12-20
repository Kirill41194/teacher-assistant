package com.diplom.teacher_assistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorId;

    @NotBlank(message = "Имя и фамилия обязательны")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20, message = "Телефон должен быть от 10 до 20 символов")
    private String phone;

    @Size(max = 500, message = "Описание не более 500 символов")
    private String description;

    @Size(max = 100, message = "Образование не более 100 символов")
    private String education;

    @Size(max = 100, message = "Опыт работы не более 100 символов")
    private String experience;


    private String avatarUrl;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, message = "Пароль должен содержать минимум 6 символов")
    @Column(nullable = false)
    private String passwordHash;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Обратная связь со студентами
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Student> students = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tutor_roles",
            joinColumns = @JoinColumn(name = "tutor_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>(Set.of("TUTOR"));

    @CreationTimestamp
    private LocalDateTime createdAt;

}