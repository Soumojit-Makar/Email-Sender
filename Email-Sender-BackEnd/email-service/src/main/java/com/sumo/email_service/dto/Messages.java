package com.sumo.email_service.dto;

import java.util.List;

public class Messages {
    private String message;
    private String from;
    private List<String> files;
    private String subject;
    public Messages(String message, String from, List<String> files, String subject) {
        this.message = message;
        this.from = from;
        this.files = files;
        this.subject = subject;
    }
    public Messages() {}
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public List<String> getFiles() {
        return files;
    }
    public void setFiles(List<String> files) {
        this.files = files;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public  static class  Builder{
        private String message;
        private String from;
        private List<String> files;
        private String subject;
        public Builder(String message, String from, List<String> files, String subject) {
            this.message = message;
            this.from = from;
            this.files = files;
            this.subject = subject;

        }
        public Builder() {}
        public Messages build() {
            return new Messages(message, from, files, subject);
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder from(String from) {
            this.from = from;
            return this;
        }
        public Builder files(List<String> files) {
            this.files = files;
            return this;
        }
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }


    }
    public static Builder builder(){
        return new Builder();
    }
    public Messages(Builder builder) {
        this.message = builder.message;
        this.from = builder.from;
        this.files = builder.files;
        this.subject = builder.subject;
    }

}
