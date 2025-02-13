package org.shelter.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final TelegramBot telegramBot;

    public MessageService(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendTextMessage(String chatId, String text) {
        SendMessage request = new SendMessage(chatId, text);
        try {
            telegramBot.execute(request);
            logger.info("Сообщение отправлено на chatId {}: {}", chatId, text);
        } catch (Exception e) {
            logger.error("Ошибка отправки сообщения на chatId {}: {}", chatId, e.getMessage(), e);
        }
    }
}

