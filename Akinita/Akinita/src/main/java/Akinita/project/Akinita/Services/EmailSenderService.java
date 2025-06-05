package Akinita.project.Akinita.Services;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String content, String fileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.addAttachment("akinita.png", new File("classpath:static/images/akinita.png"));       // File(fileNamePath)

        helper.setTo("bramis04chris@gmail.com");             // change meta
        helper.setSubject(subject);

        Context context = new Context();
        context.setVariable("content", content);        // me bash auyto bale to text sto html

        try(var inputStream = EmailSenderService.class.getResourceAsStream("/templates/Email.html")){
            assert inputStream != null;
            String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String htmlContent = htmlTemplate.replace("${content}", content);
            helper.setText(htmlContent, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        helper.addInline("akinita.png", new File("classpath:static/images/akinita.png"));

        mailSender.send(message);
    }
}
