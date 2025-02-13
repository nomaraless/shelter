package org.shelter.enums;

/**
 * Состояния диалога с пользователем.
 */
public enum UserStage {
    NONE,               // Нет активного диалога (главное меню)
    AWAIT_PHONE,        // Ожидание ввода номера телефона
    AWAIT_REPORT_PHOTO, // Ожидание фото для отчёта
    AWAIT_REPORT_TEXT   // Ожидание текстовой части отчёта
}
