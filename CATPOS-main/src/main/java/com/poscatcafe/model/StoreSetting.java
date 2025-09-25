package com.poscatcafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * เก็บข้อมูลตั้งค่าทั่วไปร้านค้า เช่น ชื่อร้าน ที่อยู่ และหมายเลขภาษี
 */
@Entity
public class StoreSetting {
    @Id
    private Long id = 1L;

    private String shopName;

    @Column(length = 1000)
    private String address;

    private String phone;
    private String taxId;
    private String promptpayId;

    /**
     * คืนรหัสการตั้งค่า (ใช้ค่าเดียวทั้งระบบ)
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสการตั้งค่า
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนชื่อร้านค้า
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * กำหนดชื่อร้านค้า
     */
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    /**
     * คืนที่อยู่ของร้านค้า
     */
    public String getAddress() {
        return address;
    }

    /**
     * กำหนดที่อยู่ของร้านค้า
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * คืนหมายเลขโทรศัพท์ของร้านค้า
     */
    public String getPhone() {
        return phone;
    }

    /**
     * กำหนดหมายเลขโทรศัพท์ของร้านค้า
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * คืนหมายเลขผู้เสียภาษี
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * กำหนดหมายเลขผู้เสียภาษี
     */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /**
     * คืนหมายเลขพร้อมเพย์ของร้านค้า
     */
    public String getPromptpayId() {
        return promptpayId;
    }

    /**
     * กำหนดหมายเลขพร้อมเพย์ของร้านค้า
     */
    public void setPromptpayId(String promptpayId) {
        this.promptpayId = promptpayId;
    }
}
