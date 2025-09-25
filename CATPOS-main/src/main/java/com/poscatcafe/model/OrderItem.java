package com.poscatcafe.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;

/**
 * รายการสินค้าแต่ละชิ้นภายในออเดอร์
 */
@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Product product;

    private String productName; // Store product name for receipt

    @Min(1)
    private int quantity;

    @Min(0)
    private double price;

    private double subtotal; // Store calculated subtotal

    /**
     * คืนรหัสรายการสินค้าในออเดอร์
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสรายการสินค้าในออเดอร์
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนสินค้าที่เชื่อมโยงกับรายการนี้
     */
    public Product getProduct() {
        return product;
    }

    /**
     * กำหนดสินค้าที่เชื่อมโยงกับรายการนี้
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * คืนชื่อสินค้าที่บันทึกไว้สำหรับใบเสร็จ
     */
    public String getProductName() {
        return productName;
    }

    /**
     * กำหนดชื่อสินค้าที่บันทึกไว้สำหรับใบเสร็จ
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * คืนจำนวนสินค้าที่สั่งในรายการนี้
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * กำหนดจำนวนสินค้าที่สั่งในรายการนี้
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * คืนราคาต่อหน่วยของสินค้าในออเดอร์
     */
    public double getPrice() {
        return price;
    }

    /**
     * กำหนดราคาต่อหน่วยของสินค้าในออเดอร์
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * คืนยอดรวมย่อยของรายการสินค้า
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * กำหนดยอดรวมย่อยของรายการสินค้า
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}