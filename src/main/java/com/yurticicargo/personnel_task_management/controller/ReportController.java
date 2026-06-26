package com.yurticicargo.personnel_task_management.controller;

import com.yurticicargo.personnel_task_management.dto.ReportSummaryResponse;
import com.yurticicargo.personnel_task_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponse> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    @GetMapping("/completed-tasks/export")
    public ResponseEntity<byte[]> exportCompletedTasks(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String csvContent = reportService.generateCompletedTasksCsv(startDate, endDate);

        byte[] csvBytes = ("\uFEFF" + csvContent).getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=completed-tasks-report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvBytes);
    }
}