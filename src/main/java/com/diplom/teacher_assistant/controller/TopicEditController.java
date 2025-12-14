package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.TopicDTO;
import com.diplom.teacher_assistant.entity.Topic;
import com.diplom.teacher_assistant.service.SubjectService;
import com.diplom.teacher_assistant.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicEditController {

    private final TopicService topicService;
    private final SubjectService subjectService;

    // URL: GET /topics/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditTopicForm(
            @PathVariable Long id,
            Model model) {

        // 1. Получаем тему (с проверкой прав)
        Topic topic = topicService.getTopicById(id);

        // 2. Создаем DTO
        TopicDTO topicDTO = TopicDTO.builder()
                .name(topic.getName())
                .description(topic.getDescription())
                .subjectId(topic.getSubject().getSubjectId())
                .build();

        // 3. Получаем предмет для отображения
        var subject = subjectService.getSubjectByCurrenTutor(
                topic.getSubject().getSubjectId()
        );

        // 4. Добавляем в модель
        model.addAttribute("topicDTO", topicDTO);
        model.addAttribute("topicId", id);
        model.addAttribute("subject", subject);

        return "topics/edit";  // → templates/topics/edit.html
    }

    // URL: POST /topics/edit/{id}
    @PostMapping("/edit/{id}")
    public String updateTopic(
            @PathVariable Long id,
            @Valid @ModelAttribute("topicDTO") TopicDTO topicDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.topicDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("topicDTO", topicDTO);
            return "redirect:/topics/edit/" + id;
        }

        try {
            topicService.updateTopic(id, topicDTO);

            // Перенаправляем на список тем того же предмета
            Topic updatedTopic = topicService.getTopicById(id);
            Long subjectId = updatedTopic.getSubject().getSubjectId();

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Тема успешно обновлена"
            );
            return "redirect:/subjects/" + subjectId + "/topics";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("topicDTO", topicDTO);
            return "redirect:/topics/edit/" + id;
        }
    }

    // URL: POST /topics/delete/{id}
    @PostMapping("/delete/{id}")
    public String deleteTopic(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // Получаем тему, чтобы знать subjectId для редиректа
            Topic topic = topicService.getTopicById(id);
            Long subjectId = topic.getSubject().getSubjectId();

            topicService.deleteTopic(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Тема успешно удалена"
            );
            return "redirect:/subjects/" + subjectId + "/topics";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Ошибка при удалении: " + e.getMessage()
            );
            return "redirect:/subjects"; // fallback
        }
    }
}