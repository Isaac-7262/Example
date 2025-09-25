package com.poscatcafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * แทนข้อมูลสินค้าในระบบ POS
 */
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 32)
    @Column(unique = true)
    private String code;

    @NotBlank
    @Size(max = 150)
    private String name;

    @Min(0)
    private double price;

    @Min(0)
    private int stock;

    @Size(max = 50)
    private String category;

    @Size(max = 512)
    private String imageUrl;

    /**
     * คืนรหัสสินค้า
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสสินค้า
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนรหัสสินค้าแบบสแกนหรือพิมพ์
     */
    public String getCode() {
        return code;
    }

    /**
     * กำหนดรหัสสินค้าแบบสแกนหรือพิมพ์
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * คืนชื่อสินค้า
     */
    public String getName() {
        return name;
    }

    /**
     * กำหนดชื่อสินค้า
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * คืนราคาของสินค้า
     */
    public double getPrice() {
        return price;
    }

    /**
     * กำหนดราคาของสินค้า
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * คืนจำนวนสินค้าคงเหลือ
     */
    public int getStock() {
        return stock;
    }

    /**
     * กำหนดจำนวนสินค้าคงเหลือ
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * คืนหมวดหมู่ของสินค้า
     */
    public String getCategory() {
        return category;
    }

    /**
     * กำหนดหมวดหมู่ของสินค้า
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * คืน URL ของรูปภาพสินค้า
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * กำหนด URL ของรูปภาพสินค้า
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
