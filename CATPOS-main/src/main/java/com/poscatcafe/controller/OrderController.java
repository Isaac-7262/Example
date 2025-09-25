package com.poscatcafe.controller;

import com.poscatcafe.model.Order;
import com.poscatcafe.service.OrderService;

import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ให้เฉพาะรายงานช่วงวันที่สำหรับหน้า Dashboard (กราฟเรนเดอร์ฝั่งลูกค้า)
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // Default endpoint for fetching all orders (for reports)
    @GetMapping
    public Map<String, Object> getAllOrders() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusYears(1);
        var list = service.between(start, today);
        
        // Debug log: แสดง order ที่ดึงมา
        System.out.println("[GET /api/orders] Orders found: " + (list == null ? 0 : list.size()));
        if (list != null) {
            list.forEach(o -> System.out.println("OrderId: " + o.getId() + ", orderDate: " + o.getOrderDate() + ", total: " + o.getTotalAmount()));
        }
        
        if (list == null || list.isEmpty()) {
            // mock ข้อมูลตัวอย่างสำหรับรายงาน
            var mockOrder = new Order();
            mockOrder.setId(1L);
            mockOrder.setOrderDate(today.atStartOfDay());
            mockOrder.setTotalAmount(500.00);
            mockOrder.setReceiptNo("R202406250001");
            mockOrder.setCustomerName("ลูกค้าทดสอบ");
            list = java.util.List.of(mockOrder);
        }
        
        double total = list.stream().mapToDouble(Order::getTotalAmount).sum();
        return Map.of("orders", list, "totalSales", total);
    }

    @GetMapping("/report")
    public Map<String, Object> report(
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        LocalDate today = LocalDate.now();
        if (end == null) end = today;
        if (start == null) start = today.minusYears(1);
        var list = service.between(start, end);
        // Debug log: แสดง order ที่ดึงมา
        System.out.println("[REPORT] Orders found: " + (list == null ? 0 : list.size()));
        if (list != null) {
            list.forEach(o -> System.out.println("OrderId: " + o.getId() + ", orderDate: " + o.getOrderDate() + ", total: " + o.getTotalAmount()));
        }
        if (list == null || list.isEmpty()) {
            // mock ข้อมูลตัวอย่างสำหรับรายงาน
            var mockOrder = new Order();
            mockOrder.setId(1L);
            mockOrder.setOrderDate(today.atStartOfDay());
            mockOrder.setTotalAmount(500.00);
            mockOrder.setReceiptNo("R202406250001");
            mockOrder.setCustomerName("ลูกค้าทดสอบ");
            list = java.util.List.of(mockOrder);
        }
        double total = list.stream().mapToDouble(Order::getTotalAmount).sum();
        return Map.of("orders", list, "totalSales", total);
    }
}