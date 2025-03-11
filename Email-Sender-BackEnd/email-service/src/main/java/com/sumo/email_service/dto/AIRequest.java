package com.sumo.email_service.dto;

public class AIRequest {
    private String subject;
    private String body;
    public AIRequest(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;

    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

}
