package com.sumo.email_service.dto;

import org.springframework.http.HttpStatus;

public class CustomResponse {
    private String message;
    private HttpStatus status;
    private boolean success;
    public CustomResponse(String message, HttpStatus status, boolean success) {
        this.message = message;
        this.status = status;
        this.success = success;
    }
   
    public CustomResponse() {}


    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public HttpStatus getStatus() {
        return status;
    }
    public void setStatus(HttpStatus status) {
        this.status = status;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public static class Builder {
        private String message;
        private HttpStatus status ;
        private boolean success;           

        public Builder() {}

        // Setter for message
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        // Setter for status
        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        // Setter for success
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        // Build method to construct the CustomResponse
        public CustomResponse build() {
            return new CustomResponse(this);
        }
    }
    public CustomResponse(Builder builder) {
        this.message = builder.message;
        this.status = builder.status;
        this.success = builder.success;
    }
    public static Builder builder(){
        return new Builder();
    }

}
