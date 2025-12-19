package com.diplom.teacher_assistant.service;

import com.diplom.teacher_assistant.entity.Tutor;
import com.diplom.teacher_assistant.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final TutorRepository tutorRepository;

    public Tutor getCurrentTutor() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String email = userDetails.getUsername();
        return tutorRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(
                        "Репетитор с email " + email + " не найден"));
    }

    public Long getCurrentTutorId() {
        return getCurrentTutor().getTutorId();
    }

    public String getCurrentTutorFullName(){
        return getCurrentTutor().getFullName();
    }

}