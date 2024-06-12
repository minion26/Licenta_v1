package com.example.licentav1.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {


    private final MailSender mailSender;

    @Autowired
    public EmailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMessage(SimpleMailMessage simpleMailMessage) {
        this.mailSender.send(simpleMailMessage);
    }


    public void sendInitialPassword(String email, String password) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jitcadiana6@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Reset Password");
        simpleMailMessage.setText("Your password is: " + password);
        this.mailSender.send(simpleMailMessage);
    }
}
