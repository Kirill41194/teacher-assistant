package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.TutorProfileDTO;
import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.service.SecurityService;
import com.diplom.teacher_assistant.service.TutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tutor")
@RequiredArgsConstructor
public class TutorController {

    private final SecurityService securityService;
    private final TutorService tutorService;

    @GetMapping("/profile")
    public String showTutorProfile(Model model) {
        Tutor tutor = securityService.getCurrentTutor();

        TutorProfileDTO tutorDTO = TutorProfileDTO.builder()
                .fullName(tutor.getFullName())
                .email(tutor.getEmail())
                .phone(tutor.getPhone())
                .description(tutor.getDescription())
                .education(tutor.getEducation())
                .experience(tutor.getExperience())
                .avatarUrl(tutor.getAvatarUrl())
                .build();

        model.addAttribute("tutor", tutor);
        model.addAttribute("tutorDTO", tutorDTO);
        return "tutor/profile";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model) {
        Tutor tutor = securityService.getCurrentTutor();

        TutorProfileDTO tutorDTO = TutorProfileDTO.builder()
                .fullName(tutor.getFullName())
                .email(tutor.getEmail())
                .phone(tutor.getPhone())
                .description(tutor.getDescription())
                .education(tutor.getEducation())
                .experience(tutor.getExperience())
                .avatarUrl(tutor.getAvatarUrl())
                .build();

        model.addAttribute("tutorDTO", tutorDTO);
        return "tutor/edit";
    }


    @PostMapping("/edit")
    public String updateProfile(
            @Valid @ModelAttribute("tutorDTO") TutorProfileDTO tutorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== ОБРАБОТКА POST /tutor/edit ===");
        System.out.println("Получен DTO: " + tutorDTO);
        System.out.println("Ошибки валидации? " + bindingResult.hasErrors());

        if (bindingResult.hasErrors()) {
            System.out.println("НАЙДЕНЫ ОШИБКИ ВАЛИДАЦИИ:");
            bindingResult.getAllErrors().forEach(error ->
                    System.out.println(" - " + error.getDefaultMessage()));

            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.tutorDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            System.out.println("Редирект на /tutor/edit с ошибками");
            return "redirect:/tutor/edit";
        }

        try {
            System.out.println("Вызываем сервис для обновления...");
            tutorService.updateTutorInfo(tutorDTO);
            System.out.println("Обновление успешно!");
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");
            return "redirect:/tutor/profile";

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка в сервисе: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            return "redirect:/tutor/edit";
        }
    }
}