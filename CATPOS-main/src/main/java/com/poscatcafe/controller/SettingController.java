package com.poscatcafe.controller;

import com.poscatcafe.model.StoreSetting;
import com.poscatcafe.service.StoreSettingService;

import org.springframework.web.bind.annotation.*;

/**
 * จัดการการตั้งค่าร้าน เช่น ข้อมูลทั่วไปของร้านและโลโก้
 */
@RestController
@RequestMapping("/api/settings")
public class SettingController {
    private final StoreSettingService service;

    /**
     * สร้างคอนโทรลเลอร์พร้อมบริการจัดการข้อมูลการตั้งค่าร้าน
     */
    public SettingController(StoreSettingService service) {
        this.service = service;
    }

    /**
     * ดึงข้อมูลการตั้งค่าร้านทั้งหมด
     */
    @GetMapping
    public StoreSetting get() {
        return service.get();
    }

    /**
     * บันทึกหรืออัปเดตข้อมูลการตั้งค่าร้าน
     */
    @PutMapping
    public StoreSetting save(@RequestBody StoreSetting setting) {
        return service.save(setting);
    }
}
