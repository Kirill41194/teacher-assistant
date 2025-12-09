package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.dto.TutorRegistrationDTO;
import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository tutorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Tutor registerTutor(TutorRegistrationDTO registrationDTO) {
        // Проверка совпадения паролей
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        // Проверка уникальности email
        if (tutorRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        // Создание нового репетитора
        Tutor tutor = new Tutor();
        tutor.setFullName(registrationDTO.getFullName());
        tutor.setEmail(registrationDTO.getEmail());
        tutor.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        tutor.setIsActive(true);

        return tutorRepository.save(tutor);
    }

    public boolean emailExists(String email) {
        return tutorRepository.existsByEmail(email);
    }
}