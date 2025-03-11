package com.sumo.email_service.controller;

import com.sumo.email_service.dto.*;
import com.sumo.email_service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/email")
@CrossOrigin("http://localhost:5173/")
public class EmailController {
    Logger log= LoggerFactory.getLogger(EmailController.class);
    EmailService emailService;
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request){
        emailService.sendEmailWithHtml(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(
                CustomResponse
                        .builder()
                            .message("Message Send Successfully")
                            .status(HttpStatus.OK)
                            .success(true)
                        .build()
        );

    }
    @PostMapping(value = "/send-with-file", consumes = {"multipart/form-data"})
    public ResponseEntity<?> sendEmailWithFile(
            @RequestPart("request") EmailRequest request,
            @RequestPart("file") MultipartFile file) throws IOException {
        log.info("File received: {}", file.getOriginalFilename());
        log.info("File type: {}", file.getContentType());

        emailService.sendEmailWithFile(
                request.getTo(),
                request.getSubject(),
                request.getBody(),
                file.getInputStream(),
                file.getOriginalFilename()
        );

        return ResponseEntity.ok(CustomResponse.builder()
                .message("Message Sent Successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build());
    }
    @GetMapping("/get")
    public ResponseEntity<List<Messages>> getEmail(){
        log.info("Get Email");
        return ResponseEntity.ok(emailService.getInboxMessages());
    }
    @PostMapping("/process")
    public ResponseEntity<AIResponse> processEmail(@RequestBody AIRequest request){

        return ResponseEntity.ok( emailService.processEmail(request));
    }

}
