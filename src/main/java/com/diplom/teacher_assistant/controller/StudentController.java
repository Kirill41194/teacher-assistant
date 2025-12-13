package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.StudentDTO;
import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = studentService.getStudentByCurrentTutor();
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("studentDTO")) {
            model.addAttribute("studentDTO", new StudentDTO());
        }
        return "students/create";
    }

    @PostMapping("/create")
    public String createStudent(
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.studentDTO", bindingResult);
            redirectAttributes.addFlashAttribute("studentDTO", studentDTO);
            return "redirect:/students/create";
        }

        try {
            studentService.createNewStudent(studentDTO);
            redirectAttributes.addFlashAttribute("success", "Студент успешно добавлен");
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("studentDTO", studentDTO);
            return "redirect:/students/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);

        // Создаем DTO из Entity
        StudentDTO studentDTO = StudentDTO.builder()
                .fullName(student.getFullName())
                .email(student.getEmail())
                .telegram(student.getTelegram())
                .age(student.getAge())
                .level(student.getLevel())
                .notes(student.getNotes())
                .build();

        model.addAttribute("studentDTO", studentDTO);
        model.addAttribute("studentId", id);
        return "students/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(
            @PathVariable Long id,
            @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.studentDTO", bindingResult);
            redirectAttributes.addFlashAttribute("studentDTO", studentDTO);
            return "redirect:/students/edit/" + id;
        }

        try {
            studentService.updateStudent(id, studentDTO);
            redirectAttributes.addFlashAttribute("success", "Данные студента обновлены");
            return "redirect:/students";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("studentDTO", studentDTO);
            return "redirect:/students/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Студент удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении студента: " + e.getMessage());
        }

        return "redirect:/students";
    }
}