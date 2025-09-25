package com.poscatcafe.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * เก็บข้อมูลที่ผู้ใช้กรอกสำหรับเข้าสู่ระบบ
 */
public class LoginRequestDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    /**
     * สร้างคำขอเข้าสู่ระบบแบบเปล่า
     */
    public LoginRequestDTO() {}

    /**
     * สร้างคำขอเข้าสู่ระบบพร้อมชื่อผู้ใช้และรหัสผ่าน
     */
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * คืนชื่อผู้ใช้ที่ส่งมาเข้าสู่ระบบ
     */
    public String getUsername() {
        return username;
    }

    /**
     * กำหนดชื่อผู้ใช้สำหรับเข้าสู่ระบบ
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * คืนรหัสผ่านที่ส่งมาเข้าสู่ระบบ
     */
    public String getPassword() {
        return password;
    }

    /**
     * กำหนดรหัสผ่านสำหรับเข้าสู่ระบบ
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
