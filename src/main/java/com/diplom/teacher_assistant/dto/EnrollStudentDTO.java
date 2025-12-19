package com.diplom.teacher_assistant.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class EnrollStudentDTO {

    @NotEmpty(message = "Выберите хотя бы один предмет")
    private List<Long> subjectIds;
}
