package com.diplom.teacher_assistant.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    @NotBlank(message = "ФИО обязательно")
    @Size(min = 2, max = 100, message = "ФИО должно быть от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    private String telegram;

    private String level;

    @NotNull(message = "Возраст обязателен")
    @Range(min = 10, max = 90, message = "Возраст должен быть от 10 до 90 лет")
    private Integer age;

    @Size(max = 500, message = "Заметки не должны превышать 500 символов")
    private String notes;
}