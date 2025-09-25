package com.poscatcafe.dto;

/**
 * ใช้แสดงข้อมูลสะสมแต้มและสิทธิ์ของลูกค้า
 */
public class LoyaltyDTO {
    private Long customerId;
    private String customerName;
    private Integer currentPoints;
    private Integer redeemableCoupons;
    private Integer pointsToNextCoupon;
    private Double progressPercentage;

    /**
     * สร้างวัตถุผลสะสมแต้มแบบเปล่า
     */
    public LoyaltyDTO() {}

    /**
     * สร้างวัตถุผลสะสมแต้มพร้อมคำนวณข้อมูลที่เกี่ยวข้อง
     */
    public LoyaltyDTO(Long customerId, String customerName, Integer currentPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.currentPoints = currentPoints;
        this.redeemableCoupons = currentPoints / 100; // 100 points = 1 coupon
        this.pointsToNextCoupon = 100 - (currentPoints % 100);
        this.progressPercentage = ((currentPoints % 100) / 100.0) * 100;
    }

    /**
     * คืนรหัสลูกค้าเจ้าของแต้ม
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * กำหนดรหัสลูกค้าเจ้าของแต้ม
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * คืนชื่อของลูกค้า
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * กำหนดชื่อของลูกค้า
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * คืนจำนวนแต้มสะสมปัจจุบัน
     */
    public Integer getCurrentPoints() {
        return currentPoints;
    }

    /**
     * กำหนดแต้มสะสมและคำนวณสิทธิ์ที่เกี่ยวข้องใหม่
     */
    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
        this.redeemableCoupons = currentPoints / 100;
        this.pointsToNextCoupon = 100 - (currentPoints % 100);
        this.progressPercentage = ((currentPoints % 100) / 100.0) * 100;
    }

    /**
     * คืนจำนวนคูปองที่สามารถแลกได้จากแต้มปัจจุบัน
     */
    public Integer getRedeemableCoupons() {
        return redeemableCoupons;
    }

    /**
     * กำหนดจำนวนคูปองที่สามารถแลกได้
     */
    public void setRedeemableCoupons(Integer redeemableCoupons) {
        this.redeemableCoupons = redeemableCoupons;
    }

    /**
     * คืนจำนวนแต้มที่ต้องสะสมเพิ่มเพื่อรับคูปองถัดไป
     */
    public Integer getPointsToNextCoupon() {
        return pointsToNextCoupon;
    }

    /**
     * กำหนดจำนวนแต้มที่ต้องสะสมเพิ่มเพื่อรับคูปองถัดไป
     */
    public void setPointsToNextCoupon(Integer pointsToNextCoupon) {
        this.pointsToNextCoupon = pointsToNextCoupon;
    }

    /**
     * คืนเปอร์เซ็นต์ความคืบหน้าไปยังคูปองถัดไป
     */
    public Double getProgressPercentage() {
        return progressPercentage;
    }

    /**
     * กำหนดเปอร์เซ็นต์ความคืบหน้าสำหรับการแลกคูปอง
     */
    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}
