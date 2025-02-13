package org.shelter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reports API", description = "Управление отчетами приюта")
public class ReportController {

    private final ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "Получение списка необработанных отчетов",
            description = "Возвращает список всех отчетов, которые еще не обработаны",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список необработанных отчетов",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DailyReport.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
            }
    )
    @GetMapping("/pending")
    public ResponseEntity<List<DailyReport>> getPendingReports() {
        logger.info("Получение списка необработанных отчетов");
        List<DailyReport> reports = reportService.getPendingReports();
        logger.info("Найдено {} необработанных отчетов", reports.size());
        return ResponseEntity.ok(reports);
    }

    @Operation(
            summary = "Пометка отчета как обработанного",
            description = "Отмечает отчет по заданному ID как обработанный",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отчет успешно помечен как обработанный"),
                    @ApiResponse(responseCode = "404", description = "Отчет не найден")
            }
    )
    @PostMapping("/{reportId}/mark-processed")
    public ResponseEntity<Void> markReportAsProcessed(@PathVariable Long reportId) {
        logger.info("Пометка отчета с ID {} как обработанного", reportId);
        reportService.markAsProcessed(reportId);
        logger.info("Отчет с ID {} успешно обработан", reportId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получение отчета по идентификатору",
            description = "Возвращает отчет по его идентификатору",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Отчет найден",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = DailyReport.class))),
                    @ApiResponse(responseCode = "404", description = "Отчет не найден")
            }
    )
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

    @Operation(
            summary = "Уведомление о ненадлежащем заполнении отчета",
            description = "Отправляет уведомление о проблеме с отчетом",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Уведомление отправлено"),
                    @ApiResponse(responseCode = "404", description = "Отчет не найден")
            }
    )
    @PostMapping("/{reportId}/notify")
    public ResponseEntity<Void> notifyInvalidReport(@PathVariable Long reportId, @RequestBody String message) {
        logger.info("Уведомление о проблеме с отчетом ID {}. Сообщение: {}", reportId, message);
        reportService.notifyInvalidReport(reportId, message);
        logger.info("Уведомление для отчета с ID {} успешно отправлено", reportId);
        return ResponseEntity.ok().build();
    }
}
