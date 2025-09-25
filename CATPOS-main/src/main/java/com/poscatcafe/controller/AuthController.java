package com.poscatcafe.controller;

import com.poscatcafe.dto.LoginRequestDTO;
import com.poscatcafe.dto.LoginResponseDTO;
import com.poscatcafe.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * จัดการการยืนยันตัวตน เช่น การล็อกอิน ล็อกเอาต์ และตรวจสอบเซสชันของผู้ใช้
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * สร้างคอนโทรลเลอร์สำหรับเรียกใช้บริการยืนยันตัวตน
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * รับคำขอเข้าสู่ระบบและส่งต่อให้บริการตรวจสอบข้อมูลผู้ใช้
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * ล้างเซสชันของผู้ใช้เพื่อล็อกเอาต์ออกจากระบบ
     */
    @PostMapping("/logout")
    public Map<String, Object> logout(
            @RequestHeader(value = "Authorization", required = false) String sessionToken) {
        authService.logout(sessionToken);
        return Map.of("success", true, "message", "ออกจากระบบสำเร็จ");
    }

    /**
     * ตรวจสอบว่าโทเคนเซสชันที่ส่งมานั้นยังใช้งานได้หรือไม่
     */
    @GetMapping("/validate")
    public Map<String, Object> validateSession(
            @RequestHeader(value = "Authorization", required = false) String sessionToken) {
        boolean isValid = authService.isValidSession(sessionToken);
        return Map.of("valid", isValid);
    }

}
