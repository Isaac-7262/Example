package com.poscatcafe.dto;

import java.util.List;

/**
 * เก็บข้อมูลคำขอชำระเงินจากหน้าขาย เช่น รายการสินค้าและวิธีการชำระ
 */
public class PaymentRequestDTO {
    private List<CartItemDTO> items;
    private Long customerId;
    private Boolean useCoupon;
    private String paymentMethod; // "cash" or "qr"
    private Double cashReceived; // for cash payments

    /**
     * สร้างวัตถุคำขอชำระเงินแบบเปล่า
     */
    public PaymentRequestDTO() {}

    /**
     * คืนรายการสินค้าในคำสั่งซื้อ
     */
    public List<CartItemDTO> getItems() {
        return items;
    }

    /**
     * กำหนดรายการสินค้าในคำสั่งซื้อ
     */
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    /**
     * คืนรหัสลูกค้าที่เกี่ยวข้องกับการชำระเงิน
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * กำหนดรหัสลูกค้าที่เกี่ยวข้องกับการชำระเงิน
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * ระบุว่าต้องการใช้คูปองหรือไม่
     */
    public Boolean getUseCoupon() {
        return useCoupon;
    }

    /**
     * กำหนดว่าการชำระเงินครั้งนี้จะใช้คูปองหรือไม่
     */
    public void setUseCoupon(Boolean useCoupon) {
        this.useCoupon = useCoupon;
    }

    /**
     * คืนรูปแบบการชำระเงิน เช่น เงินสดหรือ QR
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * กำหนดรูปแบบการชำระเงิน เช่น เงินสดหรือ QR
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * คืนจำนวนเงินสดที่ได้รับจากลูกค้า (สำหรับจ่ายเงินสด)
     */
    public Double getCashReceived() {
        return cashReceived;
    }

    /**
     * กำหนดจำนวนเงินสดที่ได้รับจากลูกค้า (สำหรับจ่ายเงินสด)
     */
    public void setCashReceived(Double cashReceived) {
        this.cashReceived = cashReceived;
    }
}
