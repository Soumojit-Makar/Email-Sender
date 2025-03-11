package com.sumo.email_service.service;

import com.sumo.email_service.dto.AIRequest;
import com.sumo.email_service.dto.AIResponse;
import com.sumo.email_service.dto.Messages;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface EmailService {
    //send email to single person
    void sendEmail(String to, String subject, String message);
    //send email to multiple person
    void sendEmail(String []to, String subject, String message);
    //send email with html
    void sendEmailWithHtml(String to , String subject, String htmlContent);
    //send email with file
    void sendEmailWithFile(String to, String subject, String message, File file);
    //send InputStream
    void sendEmailWithFile(String to, String subject, String message, InputStream inputStream,String fileName);
    //Get Messages
    List<Messages> getInboxMessages();
    AIResponse processEmail(AIRequest aiRequest);
}
