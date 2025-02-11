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
        InlineKeyboardButton infoShelterButton = new InlineKeyboardButton("Узнать информацию о приюте")
                .callbackData(BotCommand.INFO_SHELTER.getCallbackData());
        InlineKeyboardButton howToAdoptButton = new InlineKeyboardButton("Как взять животное из приюта")
                .callbackData(BotCommand.HOW_TO_ADOPT.getCallbackData());
        InlineKeyboardButton sendReportButton = new InlineKeyboardButton("Прислать отчёт о питомце")
                .callbackData(BotCommand.SEND_REPORT.getCallbackData());
        InlineKeyboardButton callVolunteerButton = new InlineKeyboardButton("Позвать волонтёра")
                .callbackData(BotCommand.CALL_VOLUNTEER.getCallbackData());

        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{ infoShelterButton },
                new InlineKeyboardButton[]{ howToAdoptButton },
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