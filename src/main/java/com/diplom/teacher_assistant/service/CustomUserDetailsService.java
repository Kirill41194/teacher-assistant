package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TutorRepository tutorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Tutor tutor = tutorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));

        // Получаем isActive через getter (Lombok должен сгенерировать getIsActive())
        if (tutor.getIsActive() == null || !tutor.getIsActive()) {
            throw new UsernameNotFoundException("Пользователь неактивен: " + email);
        }

        return new User(
                tutor.getEmail(),
                tutor.getPasswordHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TUTOR"))
        );
    }
}