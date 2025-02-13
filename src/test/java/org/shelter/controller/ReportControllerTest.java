package org.shelter.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shelter.model.DailyReport;
import org.shelter.model.User;
import org.shelter.service.ReportService;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPendingReports() {
        // Mock data
        User user = new User();
        DailyReport report1 = new DailyReport(1L, user, "/path1.jpg", "Test text", false, true, true, LocalDateTime.now());
        DailyReport report2 = new DailyReport(2L, user, "/path2.jpg", "Test text2", false, true, true, LocalDateTime.now());
        when(reportService.getPendingReports()).thenReturn(Arrays.asList(report1, report2));

        ResponseEntity<List<DailyReport>> response = reportController.getPendingReports();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reportService, times(1)).getPendingReports();
    }

    @Test
    void testMarkReportAsProcessed() {
        Long reportId = 1L;

        ResponseEntity<Void> response = reportController.markReportAsProcessed(reportId);

        assertEquals(200, response.getStatusCodeValue());
        verify(reportService, times(1)).markAsProcessed(reportId);
    }

    @Test
    void testGetReportById_Found() {
        Long reportId = 1L;
        User user = new User();
        DailyReport mockReport = new DailyReport(1L, user, "/path.jpg", "Valid Report", true, true, true, LocalDateTime.now());
        when(reportService.getReportById(reportId)).thenReturn(mockReport);

        ResponseEntity<DailyReport> response = reportController.getReportById(reportId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(reportId, response.getBody().getId());
        verify(reportService, times(1)).getReportById(reportId);
    }

    @Test
    void testGetReportById_NotFound() {
        Long reportId = 999L;
        when(reportService.getReportById(reportId)).thenReturn(null);

        ResponseEntity<DailyReport> response = reportController.getReportById(reportId);

        assertEquals(404, response.getStatusCodeValue());
        verify(reportService, times(1)).getReportById(reportId);
    }

    @Test
    void testNotifyInvalidReport() {
        Long reportId = 1L;
        String message = "Report is incomplete";

        ResponseEntity<Void> response = reportController.notifyInvalidReport(reportId, message);

        assertEquals(200, response.getStatusCodeValue());
        verify(reportService, times(1)).notifyInvalidReport(reportId, message);
    }
}
