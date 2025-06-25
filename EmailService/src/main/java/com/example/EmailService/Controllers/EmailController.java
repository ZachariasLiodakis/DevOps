package com.example.EmailService.Controllers;

import com.example.EmailService.EmailRequest;
import com.example.EmailService.Services.EmailSenderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@CrossOrigin
public class EmailController {

    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailSenderService.sendMail(request.getTo(), request.getSubject(), request.getContent());
            return ResponseEntity.ok("Email sent successfully");
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("Email sending failed: " + e.getMessage());
        }
    }
}
