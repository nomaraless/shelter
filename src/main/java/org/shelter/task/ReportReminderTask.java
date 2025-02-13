package org.shelter.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.shelter.service.ReportService;

@Component
public class ReportReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(ReportReminderTask.class);
    private final ReportService reportService;

    public ReportReminderTask(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Планировщик, который запускается каждый день в 22:00.
     * Можно изменить расписание по необходимости.
     */
    @Scheduled(cron = "0 0 20 * * *")
    public void checkReportsAndSendReminders() {
        logger.info("Запуск проверки отчётов на предмет отсутствия обновлений.");
        reportService.checkAndNotifyMissingReports();
    }
}
