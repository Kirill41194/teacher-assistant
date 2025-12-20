package com.diplom.teacher_assistant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")

    private String fromEmail;
    public void sendPasswordResetEmail(String toEmail, String tutorName, String resetPass) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Сброс пароля - Teacher Assistant");

        String text = String.format("""
            Здравствуйте, %s!
            
            Вы получили это письмо, потому что администратор сбросил ваш пароль в системе Teacher Assistant.
            
            Ваш новый пароль для входа:
            %s
            
            Если вы не запрашивали сброс пароля, проигнорируйте это письмо.
            
            С уважением,
            Команда Teacher Assistant
            """, tutorName, resetPass);

        message.setText(text);

        mailSender.send(message);
    }

    public void sendNewAccountEmail(String toEmail, String tutorName, String temporaryPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Ваш аккаунт в Teacher Assistant");

        String text = String.format("""
            Здравствуйте, %s!
            
            Администратор создал для вас аккаунт в системе Teacher Assistant.
            
            Ваши данные для входа:
            Email: %s
            Временный пароль: %s
            
            Рекомендуем сменить пароль после первого входа в систему.
            
            Для входа перейдите по ссылке: http://localhost:8080/login
            
            С уважением,
            Команда Teacher Assistant
            """, tutorName, toEmail, temporaryPassword);

        message.setText(text);

        mailSender.send(message);
    }
}