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

import java.util.List;

@Controller
@RequestMapping("/subjects/{subjectId}/topics")  // Основной префикс с subjectId
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final SubjectService subjectService;

    // URL: GET /subjects/{subjectId}/topics
    @GetMapping
    public String listTopics(
            @PathVariable Long subjectId,
            Model model) {

        var subject = subjectService.getSubjectByCurrenTutor(subjectId);

        List<Topic> topics = topicService.getTopicsBySubjectId(subjectId);

        model.addAttribute("topics", topics);
        model.addAttribute("subject", subject);
        model.addAttribute("subjectId", subjectId);

        return "topics/list";
    }

    @GetMapping("/create")
    public String showCreateTopicForm(
            @PathVariable Long subjectId,
            Model model) {

        var subject = subjectService.getSubjectByCurrenTutor(subjectId);
        TopicDTO topicDTO = TopicDTO.builder()
                .subjectId(subjectId)
                .build();

        if (!model.containsAttribute("topicDTO")) {
            model.addAttribute("topicDTO", topicDTO);
        }

        model.addAttribute("subject", subject);

        return "topics/create";
    }

    @PostMapping("/create")
    public String createTopic(
            @PathVariable Long subjectId,
            @Valid @ModelAttribute("topicDTO") TopicDTO topicDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        // 1. Устанавливаем subjectId из URL
        topicDTO.setSubjectId(subjectId);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.topicDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("topicDTO", topicDTO);
            return "redirect:/subjects/" + subjectId + "/topics/create";
        }

        try {
            topicService.createNewTopic(topicDTO);
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Тема успешно добавлена"
            );
            return "redirect:/subjects/" + subjectId + "/topics";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("topicDTO", topicDTO);
            return "redirect:/subjects/" + subjectId + "/topics/create";
        }
    }
}