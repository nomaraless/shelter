package org.shelter.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Перечисление команд бота с генерацией соответствующих кнопок.
 */
public enum BotCommand {
    INFO_SHELTER_DOG("INFO_SHELTER_DOG", "Узнать информацию о приюте для собак"),
    INFO_SHELTER_CAT("INFO_SHELTER_CAT", "Узнать информацию о приюте для кошек"),
    HOW_TO_ADOPT_DOG("HOW_TO_ADOPT_DOG", "Как взять собаку из приюта"),
    HOW_TO_ADOPT_CAT("HOW_TO_ADOPT_CAT", "Как взять кошку из приюта"),

    SEND_REPORT("SEND_REPORT", "Прислать отчёт о питомце"),
    CALL_VOLUNTEER("CALL_VOLUNTEER", "Позвать волонтёра"),
    ADOPTION_DASHBOARD("ADOPTION_DASHBOARD", "Панель усыновлений"),
    VOLUNTEER_DASHBOARD("VOLUNTEER_DASHBOARD", "Панель волонтёра"),
    REVIEW_REPORT("REVIEW_REPORT", "Рассмотреть отчёт");

    private static final Logger logger = LoggerFactory.getLogger(BotCommand.class);

    private final String callbackData;
    private final String buttonText;

    BotCommand(String callbackData, String buttonText) {
        this.callbackData = callbackData;
        this.buttonText = buttonText;
    }

    public String getCallbackData() {
        return callbackData;
    }

    public String getButtonText() {
        return buttonText;
    }

    /**
     * Получение команды по callbackData.
     *
     * @param callbackData значение callbackData
     * @return соответствующий {@link BotCommand}, либо {@code null}, если не найден
     */
    public static BotCommand fromCallbackData(String callbackData) {
        logger.debug("Поиск команды по callbackData: {}", callbackData);
        for (BotCommand command : BotCommand.values()) {
            if (command.callbackData.equals(callbackData)) {
                logger.debug("Команда найдена: {}", command);
                return command;
            }
        }
        logger.warn("Команда по callbackData '{}' не найдена", callbackData);
        return null;
    }

    /**
     * Создает inline-кнопку для команды.
     *
     * @return {@link InlineKeyboardButton} для текущей команды
     */
    public InlineKeyboardButton toInlineButton() {
        logger.debug("Создание кнопки для команды: {}", this);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(buttonText);
        button.setCallbackData(callbackData);
        logger.debug("Кнопка создана: текст='{}', callbackData='{}'", buttonText, callbackData);
        return button;
    }
}
