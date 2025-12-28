package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.config.SecurityConfig;
import com.diplom.teacher_assistant.dto.TutorCreateByAdminDTO;
import com.diplom.teacher_assistant.dto.TutorProfileDTO;
import com.diplom.teacher_assistant.dto.TutorRegistrationDTO;
import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.TutorRepository;
import com.diplom.teacher_assistant.service.EmailService;
import com.diplom.teacher_assistant.service.SecurityService;
import com.diplom.teacher_assistant.service.TutorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final TutorRepository tutorRepository;
    private final SecurityService securityService;
    private final TutorService tutorService;
    private final EmailService emailService;
    private final SecurityConfig securityConfig;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, HttpServletRequest request) {
        long totalTutors = tutorRepository.count();
        long activeTutors = tutorRepository.countByIsActive(true);
        long inactiveTutors = totalTutors - activeTutors;

        List<Tutor> allTutors = tutorRepository.findAll();
        long adminCount = allTutors.stream()
                .filter(t -> t.getRoles().contains("ADMIN"))
                .count();

        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        long newTutorsLastWeek = allTutors.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(weekAgo))
                .count();

        List<Tutor> recentTutors = tutorRepository.findTop10ByOrderByCreatedAtDesc();

        model.addAttribute("totalTutors", totalTutors);
        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("activeTutors", activeTutors);
        model.addAttribute("inactiveTutors", inactiveTutors);
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("newTutorsLastWeek", newTutorsLastWeek);
        model.addAttribute("recentTutors", recentTutors);

        return "admin/dashboard";
    }

    @GetMapping("/tutors/{id}/tutors-profile")
    public String showTutorProfile(@PathVariable("id") Long id, Model model){
        Tutor tutor = securityService.anotherTutorId(id);

        model.addAttribute("tutor", tutor);
        return "admin/tutors-profile";
    }


    @GetMapping("/tutors")
    public String manageTutors(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            Model model, HttpServletRequest request) {

        List<Tutor> tutors = tutorRepository.findAllByOrderByFullName();

        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            tutors = tutors.stream()
                    .filter(t -> t.getFullName().toLowerCase().contains(searchLower) ||
                            t.getEmail().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "active":
                    tutors = tutors.stream().filter(Tutor::getIsActive).collect(Collectors.toList());
                    break;
                case "inactive":
                    tutors = tutors.stream().filter(t -> !t.getIsActive()).collect(Collectors.toList());
                    break;
            }
        }

        if (role != null && !role.isEmpty()) {
            tutors = switch (role) {
                case "admin" -> tutors.stream()
                        .filter(t -> t.getRoles().contains("ADMIN"))
                        .collect(Collectors.toList());
                case "tutor" -> tutors.stream()
                        .filter(t -> !t.getRoles().contains("ADMIN"))
                        .collect(Collectors.toList());
                default -> tutors;
            };
        }

        if (filter != null && !filter.isEmpty()) {
            tutors = switch (filter) {
                case "active" -> tutorRepository.findByIsActiveTrue();
                case "inactive" -> tutorRepository.findByIsActiveFalse();
                case "admins" -> tutorRepository.findByRolesContaining("ADMIN");
                case "new" -> tutorRepository.findTop10ByOrderByCreatedAtDesc();
                default -> tutors;
            };
        }

        model.addAttribute("requestURI", request.getRequestURI());
        model.addAttribute("tutors", tutors);
        model.addAttribute("filter", filter);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("role", role);

        return "admin/tutors";
    }

    @PostMapping("/tutors/{id}/toggle")
    public String toggleTutorStatus(@PathVariable Long id, RedirectAttributes ra) {
        Tutor tutor = tutorRepository.findById(id).orElseThrow();
        tutor.setIsActive(!tutor.getIsActive());
        tutorRepository.save(tutor);

        ra.addFlashAttribute("success",
                tutor.getIsActive() ? "Преподаватель активирован" : "Преподаватель деактивирован");
        return "redirect:/admin/tutors";
    }

    @PostMapping("/tutors/{id}/make-admin")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes ra) {
        Tutor tutor = tutorRepository.findById(id).orElseThrow();
        tutor.getRoles().add("ADMIN");
        tutorRepository.save(tutor);

        ra.addFlashAttribute("success",
                "Преподавателю " + tutor.getFullName() + " назначены права администратора");
        return "redirect:/admin/tutors";
    }

    @PostMapping("/tutors/{id}/remove-admin")
    public String removeAdmin(@PathVariable Long id, RedirectAttributes ra) {
        Tutor tutor = tutorRepository.findById(id).orElseThrow();
        tutor.getRoles().remove("ADMIN");
        if (tutor.getRoles().isEmpty()) {
            tutor.getRoles().add("TUTOR");
        }
        tutorRepository.save(tutor);

        ra.addFlashAttribute("success",
                "У преподавателя " + tutor.getFullName() + " сняты права администратора");
        return "redirect:/admin/tutors";
    }

    @GetMapping("/tutors/new")
    public String showCreateTutorForm(Model model) {
        if (!model.containsAttribute("tutorDTO")) {
            model.addAttribute("tutorDTO", new TutorCreateByAdminDTO());
        }
        return "admin/create-tutor";
    }

    @PostMapping("/tutors/new")
    public String createNewTutor(
            @Valid @ModelAttribute("tutorDTO") TutorCreateByAdminDTO tutorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.tutorDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            return "redirect:/admin/tutors/new";
        }

        try {
            if (!tutorDTO.getPassword().equals(tutorDTO.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
                redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
                return "redirect:/admin/tutors/new";
            }

            Tutor tutor = tutorService.registerTutorAsAdmin(tutorDTO);

            redirectAttributes.addFlashAttribute("success",
                    "Преподаватель " + tutor.getFullName() + " успешно добавлен!");
            return "redirect:/admin/tutors";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            return "redirect:/admin/tutors/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при создании аккаунта: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            return "redirect:/admin/tutors/new";
        }
    }

    @PostMapping("/tutors/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, RedirectAttributes ra) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));

        String temporaryPassword = generateTemporaryPassword();

        tutor.setPasswordHash(securityConfig.passwordEncoder().encode(temporaryPassword));
        tutorRepository.save(tutor);

        try {
            emailService.sendPasswordResetEmail(
                    tutor.getEmail(),
                    tutor.getFullName(),
                    temporaryPassword
            );

            ra.addFlashAttribute("success",
                    "Пароль сброшен. На email " + tutor.getEmail() + " отправлен новый пароль.");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error",
                    "Пароль сброшен, но не удалось отправить email. Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/tutors/" + id + "/tutors-profile";
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }
    @GetMapping("/tutors/{id}/edit-tutor")
    public String showEditFormForAnotherTutor(@PathVariable("id") Long id, Model model) {
        Tutor tutor = securityService.anotherTutorId(id);

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
        model.addAttribute("tutorId", id); // ЭТО ОБЯЗАТЕЛЬНО!
        return "admin/edit-tutor";
    }

    @PostMapping("/tutors/{id}/edit-tutor")
    public String updateProfileAnotherTutor(
            @PathVariable Long id,
            @Valid @ModelAttribute("tutorDTO") TutorProfileDTO tutorDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            System.out.println("НАЙДЕНЫ ОШИБКИ ВАЛИДАЦИИ:");
            bindingResult.getAllErrors().forEach(error ->
                    System.out.println(" - " + error.getDefaultMessage()));

            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.tutorDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("tutorId", id);
            return "redirect:/admin/tutors/" + id + "/edit-tutor";
        }

        try {
            tutorService.updateAnotherTutorInfo(id, tutorDTO);
            redirectAttributes.addFlashAttribute("success", "Профиль успешно обновлен");
            return "redirect:/admin/tutors/" + id + "/tutors-profile";

        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка в сервисе: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tutorDTO", tutorDTO);
            redirectAttributes.addFlashAttribute("tutorId", id);
            return "redirect:/admin/tutors/" + id + "/edit-tutor";
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tutorId", id);
            return "redirect:/admin/tutors";
        }
    }

    @PostMapping("/tutors/{id}/delete")
    @Transactional
    public String deleteTutor(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Tutor tutor = tutorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Преподаватель не найден"));

            Tutor currentTutor = securityService.getCurrentTutor();
            if (currentTutor.getTutorId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Нельзя удалить свой аккаунт");
                return "redirect:/admin/tutors/" + id + "/tutors-profile";
            }

            tutorRepository.delete(tutor);

            redirectAttributes.addFlashAttribute("success",
                    "Преподаватель и все связанные данные успешно удалены");
            return "redirect:/admin/tutors";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
            return "redirect:/admin/tutors";
        }
    }
}