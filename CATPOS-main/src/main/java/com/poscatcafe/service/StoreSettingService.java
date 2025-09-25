package com.poscatcafe.service;

import com.poscatcafe.model.StoreSetting;
import com.poscatcafe.repository.StoreSettingRepository;

import org.springframework.stereotype.Service;

/**
 * จัดการข้อมูลการตั้งค่าร้าน เช่น ชื่อร้านและข้อมูลติดต่อ
 */
@Service
public class StoreSettingService {
    private final StoreSettingRepository repo;

    /**
     * สร้างบริการจัดการการตั้งค่าร้านด้วยคลังข้อมูล
     */
    public StoreSettingService(StoreSettingRepository repo) {
        this.repo = repo;
    }

    /**
     * ดึงข้อมูลการตั้งค่าร้าน หากไม่พบจะสร้างข้อมูลเริ่มต้น
     */
    public StoreSetting get() {
        return repo.findById(1L)
                .orElseGet(
                        () -> {
                            var setting = new StoreSetting();
                            setting.setShopName("🐱 Cat Café & Milk Tea");
                            setting.setAddress("123/45 ... กรุงเทพฯ 10110");
                            setting.setPhone("02-123-4567");
                            setting.setTaxId("1234567890123");
                            setting.setPromptpayId("");
                            return repo.save(setting);
                        });
    }

    /**
     * บันทึกหรืออัปเดตข้อมูลการตั้งค่าร้านโดยใช้รหัสคงที่
     */
    public StoreSetting save(StoreSetting setting) {
        setting.setId(1L);
        return repo.save(setting);
    }
}
