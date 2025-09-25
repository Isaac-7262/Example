package com.poscatcafe.controller;

import com.poscatcafe.dto.LoyaltyDTO;
import com.poscatcafe.service.LoyaltyService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ให้บริการข้อมูลสะสมแต้มและสิทธิประโยชน์ของลูกค้า
 */
@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    /**
     * สร้างคอนโทรลเลอร์พร้อมบริการจัดการระบบสะสมแต้ม
     */
    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    /**
     * ค้นหาลูกค้าจากคำค้นหาเพื่อดูสถานะสะสมแต้ม
     */
    @GetMapping("/search")
    public List<LoyaltyDTO> searchCustomers(@RequestParam String query) {
        return loyaltyService.searchCustomers(query);
    }

    /**
     * ดึงรายละเอียดสะสมแต้มและประวัติของลูกค้า
     */
    @GetMapping("/detail/{customerId}")
    public LoyaltyService.LoyaltyDetailDTO getLoyaltyDetail(@PathVariable Long customerId) {
        return loyaltyService.getLoyaltyDetail(customerId);
    }
}
