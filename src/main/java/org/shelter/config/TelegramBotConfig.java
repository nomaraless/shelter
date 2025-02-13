package org.shelter.config;

import com.pengrad.telegrambot.TelegramBot;
import org.shelter.repository.AnimalRepository;
import org.shelter.repository.ShelterRepository;
import org.shelter.repository.UserRepository;
import org.shelter.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private UserStateService userStateService;

    @Autowired
    private ReportService reportService;

    private ShelterConsultService shelterConsultService;
    private AdoptionConsultService adoptionConsultService;
    @Bean
    public TelegramBotService telegramBotService() {
        DefaultBotOptions options = new DefaultBotOptions();
        return new TelegramBotService(
                options,
                userRepository,
                shelterRepository,
                animalRepository,
                userStateService,
                reportService,
                shelterConsultService,
                adoptionConsultService,
                botUsername,
                botToken
        );
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}
