package com.diplom.teacher_assistant.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TutorProfileDTO {

    @NotBlank(message = "Имя и фамилия обязательны")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String fullName;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @Pattern(regexp = "^$|^[\\d\\s\\+\\(\\)\\-]+$", message = "Некорректный формат телефона")
    @Size(max = 20, message = "Телефон должен быть до 20 символов")
    private String phone;

    @Size(max = 500, message = "Описание не более 500 символов")
    private String description;

    @Size(max = 100, message = "Образование не более 100 символов")
    private String education;

    @Size(max = 100, message = "Опыт работы не более 100 символов")
    private String experience;


    private String avatarUrl;
}
