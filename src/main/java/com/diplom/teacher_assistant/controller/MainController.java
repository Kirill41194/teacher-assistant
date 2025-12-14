package com.diplom.teacher_assistant.controller;

import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.service.SecurityService;
import com.diplom.teacher_assistant.service.StudentService;
import com.diplom.teacher_assistant.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final SecurityService securityService;
    private final StudentService studentService;
    private final SubjectService subjectService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        String tutorName = securityService.getCurrentTutorFullName();
        model.addAttribute("tutorName", tutorName);

        List<Student> students = studentService.getStudentByCurrentTutor();
        long studentCount = students.size();
        model.addAttribute("studentCount", studentCount);

        long beginnerCount = students.stream()
                .filter(s -> "beginner".equals(s.getLevel()))
                .count();
        long intermediateCount = students.stream()
                .filter(s -> "intermediate".equals(s.getLevel()))
                .count();
        long advancedCount = students.stream()
                .filter(s -> "advanced".equals(s.getLevel()))
                .count();

        model.addAttribute("beginnerCount", beginnerCount);
        model.addAttribute("intermediateCount", intermediateCount);
        model.addAttribute("advancedCount", advancedCount);

        double averageAge = students.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
        model.addAttribute("averageAge", Math.round(averageAge));

        List<Subject> subjects = subjectService.getSubjectByCurrenTutor();
        long subjectCount = subjects.size();
        model.addAttribute("subjectCount", subjectCount);


        if (!subjects.isEmpty()) {
            model.addAttribute("firstSubjectId", subjects.get(0).getSubjectId());
        }

        model.addAttribute("subjectsWithTopics", subjectService.getSubjectsWithTopicCount());

        model.addAttribute("topicCount", 0);

        List<Student> recentStudents = students.stream()
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentStudents", recentStudents);

        return "dashboard";
    }
}