package com.poscatcafe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

/**
 * แทนข้อมูลพนักงานที่สามารถเข้าสู่ระบบได้
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;

    @NotBlank
    @Size(max = 255)
    private String password; // Will be hashed

    @NotBlank
    @Size(max = 100)
    private String fullName;

    @Size(max = 20)
    private String role = "CASHIER"; // ADMIN, MANAGER, CASHIER

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 15)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String department;

    @Size(max = 20)
    private String employeeId;

    private Double salary;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Boolean active = true;

    /**
     * สร้างข้อมูลพนักงานพร้อมตั้งค่าวันที่สร้างและอัปเดต
     */
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * สร้างข้อมูลพนักงานพร้อมกำหนดข้อมูลพื้นฐานและวันที่เริ่มงาน
     */
    public User(String username, String password, String fullName, String role) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.hireDate = LocalDateTime.now();
    }

    /**
     * ปรับปรุงค่าช่อง updatedAt ทุกครั้งก่อนบันทึกข้อมูล
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * คืนรหัสพนักงาน
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสพนักงาน
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนชื่อผู้ใช้สำหรับเข้าสู่ระบบ
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
     * คืนรหัสผ่านที่เข้ารหัสของพนักงาน
     */
    public String getPassword() {
        return password;
    }

    /**
     * กำหนดรหัสผ่านของพนักงาน (ต้องเข้ารหัสก่อนจัดเก็บ)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * คืนชื่อเต็มของพนักงาน
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * กำหนดชื่อเต็มของพนักงาน
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * คืนบทบาทการทำงานของพนักงาน
     */
    public String getRole() {
        return role;
    }

    /**
     * กำหนดบทบาทการทำงานของพนักงาน
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * คืนอีเมลของพนักงาน
     */
    public String getEmail() {
        return email;
    }

    /**
     * กำหนดอีเมลของพนักงาน
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * คืนหมายเลขโทรศัพท์ของพนักงาน
     */
    public String getPhone() {
        return phone;
    }

    /**
     * กำหนดหมายเลขโทรศัพท์ของพนักงาน
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * คืนที่อยู่ของพนักงาน
     */
    public String getAddress() {
        return address;
    }

    /**
     * กำหนดที่อยู่ของพนักงาน
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * คืนแผนกที่สังกัดของพนักงาน
     */
    public String getDepartment() {
        return department;
    }

    /**
     * กำหนดแผนกที่สังกัดของพนักงาน
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * คืนรหัสพนักงานภายในองค์กร
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * กำหนดรหัสพนักงานภายในองค์กร
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * คืนเงินเดือนของพนักงาน
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * กำหนดเงินเดือนของพนักงาน
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * คืนวันที่เริ่มทำงานของพนักงาน
     */
    public LocalDateTime getHireDate() {
        return hireDate;
    }

    /**
     * กำหนดวันที่เริ่มทำงานของพนักงาน
     */
    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }

    /**
     * คืนวันที่สร้างข้อมูลพนักงานในระบบ
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * กำหนดวันที่สร้างข้อมูลพนักงานในระบบ
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * คืนวันที่ปรับปรุงข้อมูลพนักงานครั้งล่าสุด
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * กำหนดวันที่ปรับปรุงข้อมูลพนักงานครั้งล่าสุด
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * ระบุว่ายังคงใช้งานบัญชีพนักงานอยู่หรือไม่
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * กำหนดสถานะการใช้งานของบัญชีพนักงาน
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}
