package com.poscatcafe.service;

import com.poscatcafe.dto.LoginRequestDTO;
import com.poscatcafe.dto.LoginResponseDTO;
import com.poscatcafe.model.Employee;
import com.poscatcafe.repository.EmployeeRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * จัดการการเข้าสู่ระบบและการจัดการเซสชันของผู้ใช้
 */
@Service
public class AuthService {
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>(); // token -> session info

    /**
     * สร้างบริการยืนยันตัวตนพร้อมกำหนดผู้ใช้เริ่มต้น
     */
    public AuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        createDefaultEmployees();
    }

    /**
     * เก็บข้อมูลเซสชันของผู้ใช้แต่ละคน
     */
    private static class SessionInfo {
        final Long employeeId;
        final LocalDateTime createdAt;
        final LocalDateTime lastAccessed;

        /**
         * สร้างเซสชันใหม่พร้อมบันทึกเวลาปัจจุบัน
         */
        public SessionInfo(Long employeeId) {
            this.employeeId = employeeId;
            this.createdAt = LocalDateTime.now();
            this.lastAccessed = LocalDateTime.now();
        }

        /**
         * คืนสำเนาเซสชันพร้อมอัปเดตเวลาที่เข้าถึงล่าสุด
         */
        public SessionInfo updateLastAccessed() {
            return new SessionInfo(employeeId, createdAt, LocalDateTime.now());
        }

        /**
         * สร้างเซสชันจากค่าที่ระบุไว้ทั้งหมด
         */
        public SessionInfo(Long employeeId, LocalDateTime createdAt, LocalDateTime lastAccessed) {
            this.employeeId = employeeId;
            this.createdAt = createdAt;
            this.lastAccessed = lastAccessed;
        }

        /**
         * ตรวจสอบว่าเซสชันหมดอายุหรือไม่ตามเวลาที่กำหนด
         */
        public boolean isExpired() {
            return lastAccessed.isBefore(LocalDateTime.now().minusHours(8)); // 8 hour session timeout
        }
    }

    /**
     * ตรวจสอบข้อมูลเข้าสู่ระบบและสร้างเซสชันใหม่เมื่อสำเร็จ
     */
    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            Employee employee = employeeRepository.findByUsername(request.getUsername()).orElse(null);

            // Check user exists and password matches (with BCrypt hashing)
            if (employee != null && passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                // Clean up expired sessions first
                cleanupExpiredSessions();

                String sessionToken = UUID.randomUUID().toString();
                activeSessions.put(sessionToken, new SessionInfo(employee.getId()));

                LoginResponseDTO.UserDTO userDTO = new LoginResponseDTO.UserDTO(
                        employee.getId(), employee.getUsername(), employee.getName(), employee.getRole());

                LoginResponseDTO response = new LoginResponseDTO(true, "เข้าสู่ระบบสำเร็จ");
                response.setSessionToken(sessionToken);
                response.setUser(userDTO);
                return response;
            } else {
                return new LoginResponseDTO(false, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            }
        } catch (Exception e) {
            return new LoginResponseDTO(false, "เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }

    /**
     * ตรวจสอบว่าโทเคนเซสชันยังคงถูกต้องอยู่หรือไม่
     */
    public boolean isValidSession(String sessionToken) {
        if (sessionToken == null || !activeSessions.containsKey(sessionToken)) {
            return false;
        }

        SessionInfo sessionInfo = activeSessions.get(sessionToken);
        if (sessionInfo.isExpired()) {
            activeSessions.remove(sessionToken);
            return false;
        }

        // Update last accessed time
        activeSessions.put(sessionToken, sessionInfo.updateLastAccessed());
        return true;
    }

    /**
     * ยกเลิกเซสชันตามโทเคนที่ระบุ
     */
    public void logout(String sessionToken) {
        if (sessionToken != null) {
            activeSessions.remove(sessionToken);
        }
    }

    /**
     * ลบเซสชันที่หมดอายุออกจากรายการ
     */
    private void cleanupExpiredSessions() {
        activeSessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * สร้างบัญชีผู้ใช้เริ่มต้นเมื่อระบบยังไม่มีข้อมูลพนักงาน
     */
    private void createDefaultEmployees() {
        if (employeeRepository.count() == 0) {
            // Create admin user
            Employee admin = new Employee();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("ผู้ดูแลระบบ");
            admin.setRole("ADMIN");
            employeeRepository.save(admin);

            // Create manager user
            Employee manager = new Employee();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setName("ผู้จัดการ");
            manager.setRole("MANAGER");
            employeeRepository.save(manager);

            // Create cashier user
            Employee cashier = new Employee();
            cashier.setUsername("cashier");
            cashier.setPassword(passwordEncoder.encode("cashier123"));
            cashier.setName("พนักงานขาย");
            cashier.setRole("CASHIER");
            employeeRepository.save(cashier);
        }
    }

}