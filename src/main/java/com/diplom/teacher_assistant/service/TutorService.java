package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.dto.TutorProfileDTO;
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
    private final SecurityService securityService;

    @Transactional
    public void registerTutor(TutorRegistrationDTO registrationDTO) {
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }

        // Проверка уникальности email
        if (tutorRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        Tutor tutor = new Tutor();
        tutor.setFullName(registrationDTO.getFullName());
        tutor.setEmail(registrationDTO.getEmail());
        tutor.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
        tutor.setIsActive(true);

        tutorRepository.save(tutor);
    }

    public boolean emailExists(String email) {
        return tutorRepository.existsByEmail(email);
    }

    @Transactional
    public void updateTutorInfo(TutorProfileDTO dto) {
        Long tutorId = securityService.getCurrentTutorId();
        Tutor tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new IllegalArgumentException("Преподаватель не найден"));

        if (!tutor.getEmail().equals(dto.getEmail()) &&
                tutorRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже используется другим преподавателем");
        }

        tutor.setFullName(dto.getFullName());
        tutor.setEmail(dto.getEmail());
        tutor.setPhone(dto.getPhone());
        tutor.setDescription(dto.getDescription());
        tutor.setEducation(dto.getEducation());
        tutor.setExperience(dto.getExperience());
        tutor.setAvatarUrl(dto.getAvatarUrl());

        tutorRepository.save(tutor);
    }


}