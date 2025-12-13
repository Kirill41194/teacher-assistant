package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final SecurityService securityService;

    @ModelAttribute("tutorName")
    public String addTutorNameToAllModels() {
        try {
            return securityService.getCurrentTutorFullName();
        } catch (Exception e) {
            return "Гость";
        }
    }
}