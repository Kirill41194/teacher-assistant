package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.SubjectDTO;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/subjects")  // Все URL будут начинаться с /subjects
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    public String listSubjects(Model model) {
        // 1. Получаем данные
        List<Subject> subjects = subjectService.getSubjectByCurrenTutor();

        // 2. Добавляем в модель для Thymeleaf
        model.addAttribute("subjects", subjects);

        // 3. Возвращаем имя HTML-шаблона
        return "subjects/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("subjectDTO")) {
            model.addAttribute("subjectDTO", new SubjectDTO());
        }
        return "subjects/create";
    }

    @PostMapping("/create")
    public String createSubject(
            @Valid @ModelAttribute("subjectDTO") SubjectDTO subjectDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 1. Проверяем валидацию DTO (@NotBlank, @Size и т.д.)
        if (bindingResult.hasErrors()) {
            // 2. Если есть ошибки - сохраняем их и возвращаем на форму
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.subjectDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("subjectDTO", subjectDTO);
            return "redirect:/subjects/create";
        }

        try {
            // 3. Пытаемся создать предмет через сервис
            subjectService.createNewSubject(subjectDTO);

            // 4. Успех - показываем сообщение
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Предмет успешно добавлен"
            );
            return "redirect:/subjects";

        } catch (IllegalArgumentException e) {
            // 5. Ошибка бизнес-логики (например, предмет уже существует)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("subjectDTO", subjectDTO);
            return "redirect:/subjects/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        // 1. Получаем предмет по ID (с проверкой прав)
        Subject subject = subjectService.getSubjectByCurrenTutor(id);

        // 2. Создаем DTO из Entity
        SubjectDTO subjectDTO = SubjectDTO.builder()
                .name(subject.getName())
                .build();

        // 3. Добавляем в модель
        model.addAttribute("subjectDTO", subjectDTO);
        model.addAttribute("subjectId", id); // Для формы

        return "subjects/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateSubject(
            @PathVariable Long id,
            @Valid @ModelAttribute("subjectDTO") SubjectDTO subjectDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.subjectDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("subjectDTO", subjectDTO);
            return "redirect:/subjects/edit/" + id;
        }

        try {
            subjectService.updateSubject(id, subjectDTO);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Предмет успешно обновлен"
            );
            return "redirect:/subjects";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("subjectDTO", subjectDTO);
            return "redirect:/subjects/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteSubject(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            subjectService.deleteSubject(id);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Предмет успешно удален"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ошибка при удалении: " + e.getMessage()
            );
        }

        return "redirect:/subjects";
    }

}