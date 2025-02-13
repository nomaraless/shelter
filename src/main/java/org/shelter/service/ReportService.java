package org.shelter.service;

import org.shelter.model.DailyReport;
import org.shelter.model.User;
import org.shelter.repository.DailyReportRepository;
import org.shelter.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final DailyReportRepository dailyReportRepository;
    private final UserRepository userRepository;
    private final TelegramBotService telegramBotService;

    public ReportService(DailyReportRepository dailyReportRepository,
                         UserRepository userRepository,
                         @Lazy TelegramBotService telegramBotService) {
        this.dailyReportRepository = dailyReportRepository;
        this.userRepository = userRepository;
        this.telegramBotService = telegramBotService;
    }

    /**
     * Сохранение нового отчёта.
     * Метод проверяет, полно ли заполнен отчёт (наличие фото и текста).
     */
    @Transactional
    public DailyReport saveReport(DailyReport report) {
        report.setHasPhoto(report.getPhotoPath() != null && !report.getPhotoPath().isEmpty());
        report.setHasText(report.getTextReport() != null && !report.getTextReport().isEmpty());
        report.setCreatedAt(LocalDateTime.now());
        report.setProcessed(false);
        return dailyReportRepository.save(report);
    }


    /**
     * Получение всех необработанных отчётов.
     */
    public List<DailyReport> getPendingReports() {
        return dailyReportRepository.findByProcessedFalse();
    }

    /**
     * Помечает отчёт как обработанный.
     */
    @Transactional
    public void markAsProcessed(Long reportId) {
        Optional<DailyReport> optionalReport = dailyReportRepository.findById(reportId);
        if (optionalReport.isPresent()) {
            DailyReport report = optionalReport.get();
            report.setProcessed(true);
            dailyReportRepository.save(report);
        } else {
            logger.warn("Отчёт с id {} не найден для обработки.", reportId);
        }
    }

    /**
     * Получение отчёта по его идентификатору.
     */
    public DailyReport getReportById(Long reportId) {
        return dailyReportRepository.findById(reportId).orElse(null);
    }

    /**
     * Отправка сообщения о ненадлежащем заполнении отчёта.
     */
    public void notifyInvalidReport(Long reportId, String message) {
        DailyReport report = getReportById(reportId);
        if (report != null) {
            User user = report.getUserId();
            telegramBotService.sendDirectMessage(user.getChatId(),
                    "Дорогой усыновитель, " + message);
            logger.info("Уведомление о недопустимом отчёте отправлено пользователю {}.", user.getChatId());
        }
    }

    public DailyReport getLatestReportForUser(User user) {
        return dailyReportRepository.findTopByUserOrderByCreatedAtDesc(user).orElse(null);
    }


    /**
     * Проверка пользователей, не приславших отчёт в течение заданного времени.
     * Если последний отчёт пользователя старше 2 дней, отправляем напоминание,
     * а также уведомляем волонтёра.
     */
    public void checkAndNotifyMissingReports() {
        List<User> users = userRepository.findAll();
        LocalDateTime threshold = LocalDateTime.now().minusDays(2);
        for (User user : users) {
            Optional<DailyReport> lastReportOpt = dailyReportRepository.findTopByUserOrderByCreatedAtDesc(user);
            if (lastReportOpt.isPresent()) {
                DailyReport lastReport = lastReportOpt.get();
                if (lastReport.getCreatedAt().isBefore(threshold)) {
                    // Отправляем напоминание пользователю
                    telegramBotService.sendDirectMessage(user.getChatId(),
                            "Вы не присылали отчёт более 2 дней. Пожалуйста, заполните ежедневный отчёт о питомце.");
                    // Уведомляем волонтёра о задержке
                    telegramBotService.sendDirectMessage(telegramBotService.getVolunteerChatId(),
                            "Пользователь с chatId " + user.getChatId() + " не присылает отчёты более 2 дней.");
                }
            } else {
                // Если отчётов вообще нет — отправляем напоминание
                telegramBotService.sendDirectMessage(user.getChatId(),
                        "Вы не присылали ни одного отчёта. Пожалуйста, заполните ежедневный отчёт о питомце.");
                telegramBotService.sendDirectMessage(telegramBotService.getVolunteerChatId(),
                        "Пользователь с chatId " + user.getChatId() + " не присылал отчёты.");
            }
        }
    }
}
