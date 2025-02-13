package org.shelter.util;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.shelter.enums.BotCommand;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardUtil {

    /**
     * Главное меню для обычного пользователя.
     */
    public static InlineKeyboardMarkup getMainMenuKeyboard() {
        InlineKeyboardButton btnDogInfo = new InlineKeyboardButton("Информация о собаках")
                .callbackData(BotCommand.INFO_SHELTER_DOG.getCallbackData());
        InlineKeyboardButton btnCatInfo = new InlineKeyboardButton("Информация о кошках")
                .callbackData(BotCommand.INFO_SHELTER_CAT.getCallbackData());
        InlineKeyboardButton howToAdoptCatButton = new InlineKeyboardButton("Как взять кошку из приюта")
                .callbackData(BotCommand.HOW_TO_ADOPT_CAT.getCallbackData());
        InlineKeyboardButton howToAdoptDogButton = new InlineKeyboardButton("Как взять собаку из приюта")
                .callbackData(BotCommand.HOW_TO_ADOPT_DOG.getCallbackData());
        InlineKeyboardButton sendReportButton = new InlineKeyboardButton("Прислать отчёт о питомце")
                .callbackData(BotCommand.SEND_REPORT.getCallbackData());
        InlineKeyboardButton callVolunteerButton = new InlineKeyboardButton("Позвать волонтёра")
                .callbackData(BotCommand.CALL_VOLUNTEER.getCallbackData());

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{ btnCatInfo},
                new InlineKeyboardButton[]{ btnDogInfo},
                new InlineKeyboardButton[]{ howToAdoptCatButton },
                new InlineKeyboardButton[]{ howToAdoptDogButton },
                new InlineKeyboardButton[]{ sendReportButton },
                new InlineKeyboardButton[]{ callVolunteerButton }
        );
    }


    /**
     * Расширенное меню для волонтёров.
     * Добавлены кнопки для панели усыновлений, обзора отчётов и стандартных вызовов.
     */
    public static InlineKeyboardMarkup getVolunteerMenuKeyboard() {
        InlineKeyboardButton btnAdoptionDashboard = new InlineKeyboardButton("Панель усыновлений")
                .callbackData("ADOPTION_DASHBOARD");

        InlineKeyboardButton btnReviewReports = new InlineKeyboardButton("Просмотр отчётов")
                .callbackData("REVIEW_REPORT");

        InlineKeyboardButton btnCallVolunteer = new InlineKeyboardButton("Вызов волонтёра")
                .callbackData("CALL_VOLUNTEER");

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{btnAdoptionDashboard},
                new InlineKeyboardButton[]{btnReviewReports},
                new InlineKeyboardButton[]{btnCallVolunteer}
        );
    }
}