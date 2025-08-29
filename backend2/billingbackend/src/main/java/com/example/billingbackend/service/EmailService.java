package com.example.billingbackend.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    /**
     * Sends an email to the specified recipient.
     * @param to The recipient's email address.
     * @param subject The subject of the email.
     * @param body The plain text body of the email.
     */
    public void sendEmail(String to, String subject, String body) {
        // --- THIS IS THE CRITICAL FIX ---
        // Ensure the 'to' parameter is used to create the recipient Email object.
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to); // This now correctly uses the recipient's email.
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            // Use a proper logger to provide better information
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Successfully sent email to {}. Status code: {}", to, response.getStatusCode());
            } else {
                logger.error("Failed to send email to {}. Status Code: {}, Body: {}", to, response.getStatusCode(), response.getBody());
            }
        } catch (IOException ex) {
            logger.error("Error sending email to {}: {}", to, ex.getMessage());
        }
    }
}