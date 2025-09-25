package com.poscatcafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * เก็บข้อมูลลูกค้าของร้านพร้อมแต้มสะสม
 */
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 30)
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @Column(length = 1000)
    private String address;

    @Column(columnDefinition = "integer default 0")
    private Integer loyaltyPoints = 0;

    /**
     * คืนรหัสลูกค้า
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสลูกค้า
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนชื่อลูกค้า
     */
    public String getName() {
        return name;
    }

    /**
     * กำหนดชื่อลูกค้า
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * คืนหมายเลขโทรศัพท์ของลูกค้า
     */
    public String getPhone() {
        return phone;
    }

    /**
     * กำหนดหมายเลขโทรศัพท์ของลูกค้า
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * คืนอีเมลของลูกค้า
     */
    public String getEmail() {
        return email;
    }

    /**
     * กำหนดอีเมลของลูกค้า
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * คืนที่อยู่ของลูกค้า
     */
    public String getAddress() {
        return address;
    }

    /**
     * กำหนดที่อยู่ของลูกค้า
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * คืนจำนวนแต้มสะสมของลูกค้า
     */
    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    /**
     * กำหนดจำนวนแต้มสะสมของลูกค้า
     */
    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
