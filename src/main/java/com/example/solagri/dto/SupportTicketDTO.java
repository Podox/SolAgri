package com.example.solagri.dto;

public class SupportTicketDTO {

    private String subject;
    private String description;

    // Constructors, Getters, and Setters
    public SupportTicketDTO() {}

    public SupportTicketDTO(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}