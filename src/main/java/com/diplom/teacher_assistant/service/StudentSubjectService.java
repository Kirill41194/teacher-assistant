package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.entity.Student;
import com.diplom.teacher_assistant.entity.StudentSubject;
import com.diplom.teacher_assistant.entity.Subject;
import com.diplom.teacher_assistant.repository.StudentRepository;
import com.diplom.teacher_assistant.repository.StudentSubjectRepository;
import com.diplom.teacher_assistant.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StudentSubjectService {

    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final SecurityService securityService;


    public List<StudentSubject> getStudentSubjects(Long studentId) {
        Long tutorId = securityService.getCurrentTutorId();
        log.debug("Получение предметов студента ID {} для преподавателя ID {}", studentId, tutorId);

        try {
            List<StudentSubject> subjects = studentSubjectRepository
                    .findByStudentIdAndTutorId(studentId, tutorId);

            return subjects;

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Subject> getAvailableSubjectsForStudent(Long studentId) {
        Long tutorId = securityService.getCurrentTutorId();
        try {
            // Проверяем, что студент существует и принадлежит преподавателю
            studentRepository.findByStudentIdAndTutor_TutorId(studentId, tutorId)
                    .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));

            List<Subject> availableSubjects = subjectRepository
                    .findSubjectsNotEnrolledByStudent(studentId, tutorId);

            return availableSubjects;

        } catch (IllegalArgumentException e) {
            log.error("Студент не найден или доступ запрещен: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении доступных предметов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Записать студента на несколько предметов
     */
    public void enrollStudentInSubjects(Long studentId, List<Long> subjectIds) {
        Long tutorId = securityService.getCurrentTutorId();
        log.info("Запись студента ID {} на предметы: {}", studentId, subjectIds);

        if (subjectIds == null || subjectIds.isEmpty()) {
            throw new IllegalArgumentException("Не выбраны предметы для записи");
        }

        // Проверяем, что студент принадлежит текущему преподавателю
        Student student = studentRepository.findByStudentIdAndTutor_TutorId(studentId, tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден или доступ запрещен"));

        for (Long subjectId : subjectIds) {
            // Проверяем, что предмет принадлежит текущему преподавателю
            Subject subject = subjectRepository.findBySubjectIdAndTutor_TutorId(subjectId, tutorId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Предмет с ID " + subjectId + " не найден или доступ запрещен"));

            // Проверяем, что студент еще не записан на этот предмет
            if (studentSubjectRepository.existsByStudent_StudentIdAndSubject_SubjectId(studentId, subjectId)) {
                throw new IllegalArgumentException(
                        String.format("Студент '%s' уже записан на предмет '%s'",
                                student.getFullName(), subject.getName()));
            }

            // Создаем запись
            StudentSubject studentSubject = new StudentSubject();
            studentSubject.setStudent(student);
            studentSubject.setSubject(subject);
            studentSubject.setProgressLevel(0);
            studentSubject.setWeaknesses(""); // Пока пусто

            studentSubjectRepository.save(studentSubject);
            log.info("Студент '{}' записан на предмет '{}'",
                    student.getFullName(), subject.getName());
        }
    }


    public void unenrollStudentFromSubject(Long studentId, Long subjectId) {
        Long tutorId = securityService.getCurrentTutorId();
        log.info("Удаление студента ID {} с предмета ID {}", studentId, subjectId);

        // Находим запись с проверкой прав
        StudentSubject studentSubject = studentSubjectRepository
                .findByStudentIdAndSubjectIdAndTutorId(studentId, subjectId, tutorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Запись не найдена или доступ запрещен"));

        String studentName = studentSubject.getStudent().getFullName();
        String subjectName = studentSubject.getSubject().getName();

        studentSubjectRepository.delete(studentSubject);
        log.info("Студент '{}' удален с предмета '{}'", studentName, subjectName);
    }

    /**
     * Обновить прогресс студента по предмету
     */
    public void updateProgress(Long studentId, Long subjectId, Integer progressLevel) {
        Long tutorId = securityService.getCurrentTutorId();

        if (progressLevel < 0 || progressLevel > 100) {
            throw new IllegalArgumentException("Прогресс должен быть от 0 до 100%");
        }

        StudentSubject studentSubject = studentSubjectRepository
                .findByStudentIdAndSubjectIdAndTutorId(studentId, subjectId, tutorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Запись не найдена или доступ запрещен"));

        studentSubject.setProgressLevel(progressLevel);
        studentSubjectRepository.save(studentSubject);

        log.info("Обновлен прогресс студента ID {} по предмету ID {}: {}%",
                studentId, subjectId, progressLevel);
    }

    /**
     * Обновить слабые места студента по предмету
     */
    public void updateWeaknesses(Long studentId, Long subjectId, String weaknesses) {
        Long tutorId = securityService.getCurrentTutorId();

        StudentSubject studentSubject = studentSubjectRepository
                .findByStudentIdAndSubjectIdAndTutorId(studentId, subjectId, tutorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Запись не найдена или доступ запрещен"));

        studentSubject.setWeaknesses(weaknesses);
        studentSubjectRepository.save(studentSubject);

        log.info("Обновлены слабые места студента ID {} по предмету ID {}",
                studentId, subjectId);
    }

    /**
     * Проверить, записан ли студент на предмет
     */
    public boolean isStudentEnrolled(Long studentId, Long subjectId) {
        Long tutorId = securityService.getCurrentTutorId();

        return studentSubjectRepository
                .findByStudentIdAndSubjectIdAndTutorId(studentId, subjectId, tutorId)
                .isPresent();
    }

    /**
     * Получить количество предметов студента
     */
    public Long getStudentSubjectsCount(Long studentId) {
        Long tutorId = securityService.getCurrentTutorId();
        return studentSubjectRepository.countByStudentIdAndTutorId(studentId, tutorId);
    }

    /**
     * Получить студентов, не записанных на предмет
     */
    public List<Student> getStudentsNotEnrolled(Long subjectId) {
        Long tutorId = securityService.getCurrentTutorId();
        log.debug("Получение студентов, не записанных на предмет ID {}", subjectId);

        try {
            // Проверяем, что предмет существует и принадлежит преподавателю
            subjectRepository.findBySubjectIdAndTutor_TutorId(subjectId, tutorId)
                    .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));

            List<Student> availableStudents = studentRepository
                    .findStudentsNotEnrolledInSubject(subjectId, tutorId);

            log.debug("Найдено {} доступных студентов для предмета ID {}",
                    availableStudents.size(), subjectId);
            return availableStudents;

        } catch (IllegalArgumentException e) {
            log.error("Предмет не найден или доступ запрещен: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при получении доступных студентов: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<StudentSubject> getSubjectStudents(Long subjectId) {
        Long tutorId = securityService.getCurrentTutorId();
        return studentSubjectRepository.findBySubjectIdAndTutorId(subjectId, tutorId);
    }
}