package com.example.EmailService.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo("it2022071@hua.gr");             // change meta se 'to'
        helper.setSubject(subject);

        try(var inputStream = EmailSenderService.class.getResourceAsStream("/templates/Email.html")){
            assert inputStream != null;
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String htmlContent = htmlTemplate.replace("${content}", content);
            helper.setText(htmlContent, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        helper.addInline("akinita.png", new ClassPathResource("static/images/akinita.png"));

        mailSender.send(message);
    }
}
