package com.poscatcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poscatcafe.service.ReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReportsData() {
        Map<String, Object> reportsData = reportService.getReportsData();
        return ResponseEntity.ok(reportsData);
    }
}
