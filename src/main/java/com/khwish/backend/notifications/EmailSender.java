package com.khwish.backend.notifications;

import com.khwish.backend.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class EmailSender {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        List<String> toList = Collections.singletonList(to);
        sendEmail(toList, subject, body);
    }

    private void sendEmail(List<String> toList, String subject, String body) {

        SimpleMailMessage msg = new SimpleMailMessage();
        String to = String.join(", ", toList);
        msg.setFrom(Constants.EMAIL_FROM);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        try {
            javaMailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
