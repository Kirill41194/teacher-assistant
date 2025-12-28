package com.diplom.teacher_assistant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkDTO {

    @NotNull
    private Long student_id;

    @NotNull
    private Long topic_id;

    private String generatedText;

    private Integer difficulty;

    private LocalDate deadline;

    private LocalDateTime createdAt;

}
