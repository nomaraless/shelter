package org.shelter.controller;

import org.shelter.model.DailyReport;
import org.shelter.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API для управления отчетами:
 * - Получение необработанных отчетов
 * - Отметка отчетов как просмотренных
 * - Получение отчета по идентификатору
 * - Уведомление о ненадлежащем заполнении отчета
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Получение списка необработанных отчетов.
     *
     * @return Список необработанных отчетов
     */
    @GetMapping("/pending")
    public ResponseEntity<List<DailyReport>> getPendingReports() {
        logger.info("Получение списка необработанных отчетов");
        List<DailyReport> reports = reportService.getPendingReports();
        logger.info("Найдено {} необработанных отчетов", reports.size());
        return ResponseEntity.ok(reports);
    }

    /**
     * Помечает отчет как обработанный.
     *
     * @param reportId Идентификатор отчета
     * @return HTTP 200 OK в случае успешной обработки
     */
    @PostMapping("/{reportId}/mark-processed")
    public ResponseEntity<Void> markReportAsProcessed(@PathVariable Long reportId) {
        logger.info("Пометка отчета с ID {} как обработанного", reportId);
        reportService.markAsProcessed(reportId);
        logger.info("Отчет с ID {} успешно обработан", reportId);
        return ResponseEntity.ok().build();
    }

    /**
     * Получение отчета по идентификатору.
     *
     * @param reportId Идентификатор отчета
     * @return Объект отчета или HTTP 404, если не найден
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<DailyReport> getReportById(@PathVariable Long reportId) {
        logger.info("Запрос на получение отчета с ID {}", reportId);
        DailyReport report = reportService.getReportById(reportId);
        if (report == null) {
            logger.warn("Отчет с ID {} не найден", reportId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Отчет с ID {} успешно найден", reportId);
        return ResponseEntity.ok(report);
    }

    /**
     * Отправка уведомления о ненадлежащем отчете.
     *
     * @param reportId Идентификатор отчета
     * @param message  Сообщение об ошибке
     * @return HTTP 200 OK в случае успешного уведомления
     */
    @PostMapping("/{reportId}/notify")
    public ResponseEntity<Void> notifyInvalidReport(@PathVariable Long reportId, @RequestBody String message) {
        logger.info("Уведомление о проблеме с отчетом ID {}. Сообщение: {}", reportId, message);
        reportService.notifyInvalidReport(reportId, message);
        logger.info("Уведомление для отчета с ID {} успешно отправлено", reportId);
        return ResponseEntity.ok().build();
    }
}
