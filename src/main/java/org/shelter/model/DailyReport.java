package org.shelter.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "daily_reports")
public class DailyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Связь с пользователем (усыновителем)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Путь к файлу фото или URL
    @Column(name = "photo_path")
    private String photoPath;

    // Текст отчёта
    @Column(name = "text_report", columnDefinition = "TEXT")
    private String textReport;

    // Дата создания отчёта
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Флаг обработки отчёта
    @Column(name = "processed")
    private boolean processed;

    // Флаги полноты отчёта:
    @Column(name = "has_photo")
    private boolean hasPhoto;

    @Column(name = "has_text")
    private boolean hasText;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public User getUserId() {
        return user;
    }
    public void setUserId(User userId) {
        this.user = userId;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public String getTextReport() {
        return textReport;
    }
    public void setTextReport(String textReport) {
        this.textReport = textReport;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public boolean isProcessed() {
        return processed;
    }
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
    public boolean isHasPhoto() {
        return hasPhoto;
    }
    public void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }
    public boolean isHasText() {
        return hasText;
    }
    public void setHasText(boolean hasText) {
        this.hasText = hasText;
    }

    public DailyReport(Long id, User user, String photoPath, String textReport, boolean processed, boolean hasPhoto, boolean hasText, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.photoPath = photoPath;
        this.textReport = textReport;
        this.processed = processed;
        this.hasPhoto = hasPhoto;
        this.hasText = hasText;
        this.createdAt = createdAt;
    }

    public DailyReport() {
    }
}
