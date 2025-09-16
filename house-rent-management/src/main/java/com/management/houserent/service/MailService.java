package com.management.houserent.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

import java.io.File;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void sendMail(String to , String subject , String htmlBody , File attachment)throws MessagingException{

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message , true , "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("verify@scm.com");
        if(attachment != null && attachment.exists()){
            FileSystemResource fr = new FileSystemResource(attachment);
            helper.addAttachment(attachment.getName(), fr );
        }

        mailSender.send(message);


    }


}
