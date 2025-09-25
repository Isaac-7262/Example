package com.poscatcafe.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * แทนข้อมูลออเดอร์ทั้งหมด รวมถึงลูกค้าและรายการสินค้า
 */
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    private String customerName; // Store customer name for receipt

    @Column(name = "order_date")
    private LocalDateTime orderDate = LocalDateTime.now();

    private double totalAmount;

    private String paymentMethod;

    private Double discountAmount = 0.0;

    @Column(unique = true)
    private String receiptNo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items = new ArrayList<>();

    /**
     * คืนรหัสออเดอร์
     */
    public Long getId() {
        return id;
    }

    /**
     * กำหนดรหัสออเดอร์
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * คืนข้อมูลลูกค้าที่เกี่ยวข้องกับออเดอร์
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * กำหนดข้อมูลลูกค้าที่เกี่ยวข้องกับออเดอร์
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * คืนชื่อของลูกค้าที่จะแสดงบนใบเสร็จ
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * กำหนดชื่อของลูกค้าที่จะแสดงบนใบเสร็จ
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * คืนเวลาที่สร้างออเดอร์
     */
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    /**
     * กำหนดเวลาที่สร้างออเดอร์
     */
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    /**
     * คืนยอดรวมสุทธิของออเดอร์
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * กำหนดยอดรวมสุทธิของออเดอร์
     */
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * คืนวิธีการชำระเงินของออเดอร์
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * กำหนดวิธีการชำระเงินของออเดอร์
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * คืนจำนวนส่วนลดที่ใช้กับออเดอร์
     */
    public Double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * กำหนดจำนวนส่วนลดที่ใช้กับออเดอร์
     */
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * คืนเลขที่ใบเสร็จของออเดอร์
     */
    public String getReceiptNo() {
        return receiptNo;
    }

    /**
     * กำหนดเลขที่ใบเสร็จของออเดอร์
     */
    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    /**
     * คืนรายการสินค้าทั้งหมดในออเดอร์
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * กำหนดรายการสินค้าทั้งหมดในออเดอร์
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}