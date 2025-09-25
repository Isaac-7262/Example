package com.poscatcafe.dto;

/**
 * ใช้ตอบกลับผลการเข้าสู่ระบบ พร้อมข้อมูลผู้ใช้และโทเคนเซสชัน
 */
public class LoginResponseDTO {
    private Boolean success;
    private String message;
    private String sessionToken;
    private UserDTO user;

    /**
     * สร้างผลตอบกลับการเข้าสู่ระบบแบบเปล่า
     */
    public LoginResponseDTO() {}

    /**
     * สร้างผลตอบกลับการเข้าสู่ระบบพร้อมสถานะและข้อความ
     */
    public LoginResponseDTO(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * ระบุผลการเข้าสู่ระบบสำเร็จหรือไม่
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * กำหนดผลการเข้าสู่ระบบสำเร็จหรือไม่
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * คืนข้อความแจ้งผลการเข้าสู่ระบบ
     */
    public String getMessage() {
        return message;
    }

    /**
     * กำหนดข้อความแจ้งผลการเข้าสู่ระบบ
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * คืนโทเคนเซสชันหลังเข้าสู่ระบบสำเร็จ
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * กำหนดโทเคนเซสชันสำหรับผู้ใช้ที่ล็อกอิน
     */
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    /**
     * คืนข้อมูลผู้ใช้ที่เข้าสู่ระบบ
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * กำหนดข้อมูลผู้ใช้ที่เข้าสู่ระบบ
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    /**
     * เป็นข้อมูลย่อยของผู้ใช้ที่ล็อกอินเข้าใช้งาน
     */
    public static class UserDTO {
        private Long id;
        private String username;
        private String name;
        private String role;

        /**
         * สร้างข้อมูลผู้ใช้แบบเปล่า
         */
        public UserDTO() {}

        /**
         * สร้างข้อมูลผู้ใช้พร้อมค่าเริ่มต้นทั้งหมด
         */
        public UserDTO(Long id, String username, String name, String role) {
            this.id = id;
            this.username = username;
            this.name = name;
            this.role = role;
        }

        /**
         * คืนรหัสผู้ใช้ที่ล็อกอิน
         */
        public Long getId() {
            return id;
        }

        /**
         * กำหนดรหัสผู้ใช้ที่ล็อกอิน
         */
        public void setId(Long id) {
            this.id = id;
        }

        /**
         * คืนชื่อบัญชีของผู้ใช้
         */
        public String getUsername() {
            return username;
        }

        /**
         * กำหนดชื่อบัญชีของผู้ใช้
         */
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * คืนชื่อเต็มของผู้ใช้
         */
        public String getName() {
            return name;
        }

        /**
         * กำหนดชื่อเต็มของผู้ใช้
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * คืนบทบาทหรือสิทธิ์ของผู้ใช้
         */
        public String getRole() {
            return role;
        }

        /**
         * กำหนดบทบาทหรือสิทธิ์ของผู้ใช้
         */
        public void setRole(String role) {
            this.role = role;
        }
    }
}