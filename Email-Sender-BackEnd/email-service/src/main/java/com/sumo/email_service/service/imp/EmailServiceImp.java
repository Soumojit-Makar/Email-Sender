package com.sumo.email_service.service.imp;

import com.sumo.email_service.dto.AIRequest;
import com.sumo.email_service.dto.AIResponse;
import com.sumo.email_service.dto.Messages;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.sumo.email_service.service.EmailService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailServiceImp implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImp.class);
    @Value("${mail.imaps.host}")
    String host;
    @Value("${mail.imaps.port}")
    String port;
    @Value("${mail.store.protocol}")
    String protocol;
    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;
    private final JavaMailSender mailSender;
    private final ChatClient chatClient;
    public EmailServiceImp(JavaMailSender mailSender, ChatClient.Builder chatClient) {
        this.mailSender = mailSender;
        this.chatClient = chatClient.build();
    }
    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(message);
        msg.setSentDate(new Date());
        this.mailSender.send(msg);
    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(message);
        msg.setSentDate(new Date());
        this.mailSender.send(msg);
    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlContent) {
        MimeMessage msg= this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg,true,"UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            this.mailSender.send(msg);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {
        MimeMessage msg= this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(msg, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message);
            mimeMessageHelper.addAttachment(file.getName(), file);
            this.mailSender.send(msg);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message,InputStream inputStream,String fileName) {
        MimeMessage msg= this.mailSender.createMimeMessage();
        try {

            MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(msg,true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message,true);

            mimeMessageHelper.addAttachment(fileName, () -> inputStream);
            this.mailSender.send(msg);
        }
        catch (MessagingException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Messages> getInboxMessages() {
        List<Messages> list = new ArrayList<>();
        Properties config=new Properties();
        config.setProperty("mail.store.protocol",protocol);
        config.setProperty("mail.imaps.host",host);
        config.setProperty("mail.imaps.port",port);
        Session defaultInstance = Session.getDefaultInstance(config);
        try {
            Store store = defaultInstance.getStore();
            store.connect(username,password);
            Folder inbox = store.getFolder("INBOX");
            if (!inbox.isOpen()) {
                inbox.open(Folder.READ_ONLY);
            }

            Message[] messages = inbox.getMessages(1,10);
            for (Message message : messages) {
                String content = getContentFromEmailMessage(message);
                List<String> attchment=getFileFromEmailMessage(message);
                list.add(Messages
                        .builder()
                                .subject(message.getSubject())
                                .from(Arrays.toString(message.getFrom()))
                                .message(content)
                                .files(attchment)
                        .build()
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public AIResponse processEmail(AIRequest request) {
        log.info("Test1");
        String rewrittenContent = extractCleanedEmailThink(extractCleanedEmail(Objects.requireNonNull(chatClient.prompt("Fix grammar and rewrite this email professionally: " + request.getBody()).call().content())));
        log.info("Test2");
        String generatedTitle =extractCleanedEmailThink(extractCleanedEmail(Objects.requireNonNull(chatClient.prompt("Generate a suitable title based on this email content under 15 words: " + rewrittenContent).call().content())));
        log.info("Test5");
        return new AIResponse(generatedTitle, rewrittenContent);
    }

    private List<String> getFileFromEmailMessage(Message message) {
        List<String> attachment=new ArrayList<>();
        try {
            if (message.isMimeType("multipart/*")){
                Multipart multipart=(Multipart)message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart=multipart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())){
                        InputStream inputStream = bodyPart.getInputStream();
                        File file=new File("scr/main/resources/static/email"+bodyPart.getFileName());
                        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        attachment.add(file.getAbsolutePath());
                    }
                }
            }
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
        return attachment;
    }

    private String getContentFromEmailMessage(Message message) {
        try {
            if (message.isMimeType("text/plain")||message.isMimeType("text/html")){
                return message.getContent().toString();
            }
            else if (message.isMimeType("multipart/*")){
                Multipart multipart= (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")){
                        return bodyPart.getContent().toString();
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String extractCleanedEmail(String aiResponse) {
        Pattern pattern = Pattern.compile("---\\s*(.*?)\\s*---", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        String lastMatch = null;
        while (matcher.find()) {
            lastMatch = matcher.group(1).trim();
        }
        return lastMatch != null ? lastMatch : aiResponse.trim();
    }
    public static String extractCleanedEmailThink(String message) {
         Pattern THINK_PATTERN = Pattern.compile("<think>.*?</think>", Pattern.DOTALL);
            if (message == null || message.isBlank()) {
                return "No Title Generated";
            }
        return THINK_PATTERN.matcher(message).replaceAll("").trim();

    }
}
