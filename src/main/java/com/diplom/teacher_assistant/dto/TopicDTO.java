package com.diplom.teacher_assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO {
    @NotBlank(message = "Название темы не может быть пустым")
    @Size(min = 3, max = 50, message = "Название должно содержать от 3 до 50 символов")
    private String name;

    @Size(max = 100)
    private String description;

    @NotNull
    private Long subjectId;
}
