package com.diplom.teacher_assistant.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {

    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(min = 3, max = 50, message = "Название должно содержать от 5 до 50 символов")
    private String name;
}
