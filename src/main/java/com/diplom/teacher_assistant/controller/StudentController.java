package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.dto.EnrollStudentDTO;
import com.diplom.teacher_assistant.dto.StudentDTO;
import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.entity.StudentSubject;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.service.StudentService;
import com.diplom.teacher_assistant.service.StudentSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentSubjectService studentSubjectService;

    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = studentService.getStudentByCurrentTutor();
        model.addAttribute("students", students);
        return "students/list";
    }

    @GetMapping("/{id}")
    public String showStudentDetails(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        List<StudentSubject> studentSubjects = studentSubjectService.getStudentSubjects(id);
        List<Subject> availableSubjects = studentSubjectService.getAvailableSubjectsForStudent(id);

        // Создаем DTO для формы
        EnrollStudentDTO enrollDTO = new EnrollStudentDTO();

        model.addAttribute("student", student);
        model.addAttribute("studentSubjects", studentSubjects);
        model.addAttribute("availableSubjects", availableSubjects);
        model.addAttribute("enrollDTO", enrollDTO);
        return "students/profile";
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
            return "redirect:/students/" + id;
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

    @GetMapping("/{id}/subjects/enroll")
    public String showEnrollForm(
            @PathVariable Long id,
            Model model,
            @ModelAttribute("enrollDTO") EnrollStudentDTO enrollDTO,
            BindingResult bindingResult) {

        try {
            Student student = studentService.getStudentById(id);
            List<Subject> availableSubjects = studentSubjectService.getAvailableSubjectsForStudent(id);
            List<StudentSubject> enrolledSubjects = studentSubjectService.getStudentSubjects(id);

            model.addAttribute("student", student);
            model.addAttribute("availableSubjects", availableSubjects);
            model.addAttribute("enrolledSubjects", enrolledSubjects);
            model.addAttribute("currentSubjectsCount", enrolledSubjects.size());

            if (!model.containsAttribute("enrollDTO")) {
                model.addAttribute("enrollDTO", new EnrollStudentDTO());
            }

            return "students/enroll";

        } catch (IllegalArgumentException e) {
            return "redirect:/students?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{studentId}/subjects/enroll")
    public String enrollStudent(
            @PathVariable Long studentId,
            @Valid @ModelAttribute EnrollStudentDTO enrollDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.enrollDTO",
                    bindingResult
            );
            redirectAttributes.addFlashAttribute("enrollDTO", enrollDTO);
            return "redirect:/students/" + studentId + "/subjects/enroll";
        }

        try {
            studentSubjectService.enrollStudentInSubjects(studentId, enrollDTO.getSubjectIds());
            redirectAttributes.addFlashAttribute("success",
                    "Студент успешно записан на выбранные предметы");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("enrollDTO", enrollDTO);
            return "redirect:/students/" + studentId + "/subjects/enroll";
        }

        return "redirect:/students/" + studentId;
    }

    @PostMapping("/{studentId}/subjects/{subjectId}/unenroll")
    public String unenrollStudent(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            RedirectAttributes redirectAttributes) {

        try {
            studentSubjectService.unenrollStudentFromSubject(studentId, subjectId);
            redirectAttributes.addFlashAttribute("success",
                    "Студент удален с предмета");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/students/" + studentId;
    }
}