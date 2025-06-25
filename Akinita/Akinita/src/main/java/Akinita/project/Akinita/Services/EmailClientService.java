package Akinita.project.Akinita.Services;

import Akinita.project.Akinita.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailClientService {

    @Value("${email.service.url}")
    private String emailServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendEmail(String to, String subject, String content) {
        EmailRequest request = new EmailRequest(to, subject, content);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    emailServiceUrl + "/api/email/send", request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Failed to call email-service: " + e.getMessage());
            return false;
        }
    }
}
