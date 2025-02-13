package org.shelter.model;


import jakarta.persistence.*;
import org.shelter.enums.UserStage;

@Entity
@Table(name = "user_states")
public class UserState {

    @Id
    @Column(name = "chat_id", nullable = false, unique = true)
    private String chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private UserStage stage;

    public UserState() {}

    public UserState(String chatId, UserStage stage) {
        this.chatId = chatId;
        this.stage = stage;
    }

    // Геттеры и сеттеры
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public UserStage getStage() {
        return stage;
    }
    public void setStage(UserStage stage) {
        this.stage = stage;
    }
}
