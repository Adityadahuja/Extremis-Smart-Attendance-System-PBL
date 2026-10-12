package com.extremis.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing an institutional Notice.
 */
public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String category; // IMPORTANT, GENERAL, ACADEMIC
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean isRead;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public Notice() {
    }

    public Notice(Long id, String category, String title, String message, LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.category = category;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFormattedDate() {
        return createdAt != null ? createdAt.format(DATE_FMT) : "—";
    }
}
