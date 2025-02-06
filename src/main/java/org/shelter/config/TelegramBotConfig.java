package org.shelter.config;

import org.shelter.repository.AnimalRepository;
import org.shelter.repository.ShelterRepository;
import org.shelter.repository.UserRepository;
import org.shelter.service.ReportService;
import org.shelter.service.TelegramBotService;
import org.shelter.service.UserStateService;
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
                botUsername,
                botToken
        );
    }
}
