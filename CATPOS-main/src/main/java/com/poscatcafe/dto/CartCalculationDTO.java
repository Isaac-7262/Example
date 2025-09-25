package com.poscatcafe.dto;

import java.util.List;

/**
 * เป็นวัตถุสำหรับเก็บผลการคำนวณตะกร้า เช่น ยอดรวม ส่วนลด และแต้มสะสม
 */
public class CartCalculationDTO {
    private List<CartItemDTO> items;
    private Double subtotal;
    private Double discount;
    private String discountDescription;
    private Double total;
    private Integer loyaltyPointsEarned;
    private Boolean stockAvailable;
    private String errorMessage;

    /**
     * สร้างวัตถุเปล่าสำหรับบรรจุผลการคำนวณตะกร้า
     */
    public CartCalculationDTO() {}

    /**
     * คืนรายการสินค้าในตะกร้าที่ใช้คำนวณ
     */
    public List<CartItemDTO> getItems() {
        return items;
    }

    /**
     * กำหนดรายการสินค้าในตะกร้าที่ใช้คำนวณ
     */
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    /**
     * คืนยอดรวมก่อนหักส่วนลด
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * กำหนดยอดรวมก่อนหักส่วนลด
     */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * คืนจำนวนเงินส่วนลดที่ได้รับ
     */
    public Double getDiscount() {
        return discount;
    }

    /**
     * กำหนดจำนวนเงินส่วนลดที่ได้รับ
     */
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    /**
     * คืนคำอธิบายส่วนลดที่ใช้กับตะกร้า
     */
    public String getDiscountDescription() {
        return discountDescription;
    }

    /**
     * กำหนดคำอธิบายส่วนลดที่ใช้กับตะกร้า
     */
    public void setDiscountDescription(String discountDescription) {
        this.discountDescription = discountDescription;
    }

    /**
     * คืนยอดสุทธิหลังหักส่วนลด
     */
    public Double getTotal() {
        return total;
    }

    /**
     * กำหนดยอดสุทธิหลังหักส่วนลด
     */
    public void setTotal(Double total) {
        this.total = total;
    }

    /**
     * คืนจำนวนแต้มสะสมที่ได้รับจากรายการนี้
     */
    public Integer getLoyaltyPointsEarned() {
        return loyaltyPointsEarned;
    }

    /**
     * กำหนดจำนวนแต้มสะสมที่ได้รับจากรายการนี้
     */
    public void setLoyaltyPointsEarned(Integer loyaltyPointsEarned) {
        this.loyaltyPointsEarned = loyaltyPointsEarned;
    }

    /**
     * ระบุว่าสินค้าทั้งหมดมีสต็อกพร้อมขายหรือไม่
     */
    public Boolean getStockAvailable() {
        return stockAvailable;
    }

    /**
     * กำหนดสถานะการมีสต็อกของสินค้าในตะกร้า
     */
    public void setStockAvailable(Boolean stockAvailable) {
        this.stockAvailable = stockAvailable;
    }

    /**
     * คืนข้อความแสดงข้อผิดพลาดระหว่างการคำนวณ
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * กำหนดข้อความข้อผิดพลาดที่เกิดขึ้นระหว่างการคำนวณ
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
