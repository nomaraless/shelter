package org.shelter.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendPhoto;
import org.shelter.model.Shelter;
import org.shelter.repository.ShelterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShelterConsultService {

    private static final Logger logger = LoggerFactory.getLogger(ShelterConsultService.class);

    private final ShelterRepository shelterRepository;
    private final MessageService messageService;
    private final TelegramBot telegramBot;

    public ShelterConsultService(ShelterRepository shelterRepository,
                                      MessageService messageService,
                                      TelegramBot telegramBot) {
        this.shelterRepository = shelterRepository;
        this.messageService = messageService;
        this.telegramBot = telegramBot;
    }

    public void handleShelterInfo(String chatId, String shelterType) {
        Optional<Shelter> shelterOpt = shelterRepository.findByType(shelterType);
        if (shelterOpt.isEmpty()) {
            messageService.sendTextMessage(chatId, "Информация о приюте отсутствует. Обратитесь к волонтёру.");
            return;
        }
        Shelter shelter = shelterOpt.get();
        String info = String.format(
                "Информация о приюте \"%s\":\nАдрес: %s\nРежим работы: %s\nКонтакты охраны: %s\n\nРекомендации по технике безопасности:\n- Соблюдайте правила пропуска\n- На территории приюта запрещено шуметь\n- Следуйте инструкциям персонала",
                shelter.getName(), shelter.getAddress(), shelter.getWorkingHours(), shelter.getContacts()
        );
        messageService.sendTextMessage(chatId, info);
        if (shelter.getMapUrl() != null && !shelter.getMapUrl().isEmpty()) {
            try {
                SendPhoto photoMessage = new SendPhoto(chatId, shelter.getMapUrl())
                        .caption("Схема проезда к приюту");
                telegramBot.execute(photoMessage);
            } catch (Exception e) {
                logger.error("Ошибка отправки схемы проезда: ", e);
            }
        }
        messageService.sendTextMessage(chatId, "Если у вас возникли вопросы, вы можете оставить контакт для связи.");
    }
}
