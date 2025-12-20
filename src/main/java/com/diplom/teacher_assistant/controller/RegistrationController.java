package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.TutorRegistrationDTO;
import com.diplom.teacher_assistant.service.TutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final TutorService tutorService;
    private static final Logger log = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("tutor")) {
            model.addAttribute("tutor", new TutorRegistrationDTO());
        }
        return "register";
    }

    @PostMapping
    public String registerTutor(
            @Valid @ModelAttribute("tutor") TutorRegistrationDTO tutorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // Проверка валидации
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tutor", bindingResult);
            redirectAttributes.addFlashAttribute("tutor", tutorDTO);
            return "redirect:/register";
        }

        try {
            if (!tutorDTO.getPassword().equals(tutorDTO.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
                redirectAttributes.addFlashAttribute("tutor", tutorDTO);
                return "redirect:/register";
            }

            tutorService.registerTutor(tutorDTO);

            redirectAttributes.addFlashAttribute("success",
                    "Регистрация успешна! Теперь вы можете войти в систему.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            log.error("Ошибка регистрации: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tutor", tutorDTO);
            return "redirect:/register";
        } catch (Exception e) {
            log.error("Неизвестная ошибка при регистрации", e);
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при регистрации");
            redirectAttributes.addFlashAttribute("tutor", tutorDTO);
            return "redirect:/register";
        }
    }
}