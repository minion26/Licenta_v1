package com.example.licentav1.email;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender2;

    private final MailSender mailSender;


    private final  AmazonSimpleEmailService sesClient;

    @Autowired
    public EmailService(JavaMailSender mailSender2, MailSender mailSender, AmazonSimpleEmailService sesClient) {
        this.mailSender2 = mailSender2;
        this.mailSender = mailSender;
        this.sesClient = sesClient;
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

    public void sendInitialPasswordStyle(String email, String password) {
        try {
            String htmlBody = "<div style='font-family: Arial, sans-serif; margin: 0 auto; width: 80%; padding: 20px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #2A6EBB; text-align: center;'>Reset Password</h2>" +
                    "<hr style='border: none; border-top: 1px solid #ddd;'>" +
                    "<p style='color: #333333;'>Your password is: <b style='color: #FF6347;'>" + password + "</b></p>" +
                    "<p style='color: #333333;'>Please change your password after logging in.</p>" +
                    "<p style='color: #333333;'>Best,</p>" +
                    "<p style='color: #333333;'>Your Admin Team</p>" +
                    "</div>";

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(email))
                    .withMessage(new Message()
                            .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody)))
                            .withSubject(new Content().withCharset("UTF-8").withData("Reset Password")))
                    .withSource("jitcadiana6@gmail.com");

            sesClient.sendEmail(request);
        } catch (Exception e) {
            System.out.println("Error while sending mail : " + e.getMessage());
        }
    }

    public void sendReminderHomework(String email, String homeworkTitle, String courseName){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("jitcadiana6@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Homework Reminder");
        simpleMailMessage.setText("You have to submit the homework: " + homeworkTitle + " for the course: " + courseName + " tomorrow.");
        this.mailSender.send(simpleMailMessage);
        System.out.println("sending email");

    }

    public void sendReminderHomeworkStyle(String email, String homeworkTitle, String courseName) {
        try {
//            String htmlBody = "<div style='font-family: Arial, sans-serif;'>" +
//                    "<h3 style='color: #2A6EBB;'>You have to submit the homework: " + homeworkTitle + "</h3>" +
//                    "<p style='color: #333333;'>For the course: <b style='color: #FF6347;'>" + courseName + "</b> tomorrow</p>" +
//                    "</div>";
            String htmlBody = "<div style='font-family: Arial, sans-serif; margin: 0 auto; width: 80%; padding: 20px; border: 1px solid #ddd; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>" +
                    "<h2 style='color: #2A6EBB; text-align: center;'>Homework Reminder</h2>" +
                    "<hr style='border: none; border-top: 1px solid #ddd;'>" +
                    "<h3 style='color: #2A6EBB;'>Homework: " + homeworkTitle + "</h3>" +
                    "<p style='color: #333333;'>You are required to submit the above homework for the course: <b style='color: #FF6347;'>" + courseName + "</b> by tomorrow.</p>" +
                    "<p style='color: #333333;'>Please disregard this email if you have already submitted.</p>" +
                    "<p style='color: #333333;'>Best,</p>" +
                    "<p style='color: #333333;'>Your Course Team</p>" +
                    "</div>";

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(email))
                    .withMessage(new Message()
                            .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody)))
                            .withSubject(new Content().withCharset("UTF-8").withData("Homework Reminder")))
                    .withSource("jitcadiana6@gmail.com");

            sesClient.sendEmail(request);
        } catch (Exception e) {
            System.out.println("Error while sending mail : " + e.getMessage());
        }
    }
}
