package com.poscatcafe.dto;

/**
 * ใช้ตอบกลับผลการประมวลผลการชำระเงิน
 */
public class PaymentResponseDTO {
    private Boolean success;
    private String message;
    private Long orderId;
    private String receiptHtml;
    private String qrCodeImage; // Base64 encoded QR code image
    private Double changeAmount; // for cash payments
    private String errorMessage;

    /**
     * สร้างผลตอบกลับการชำระเงินแบบเปล่า
     */
    public PaymentResponseDTO() {}

    /**
     * สร้างผลตอบกลับการชำระเงินพร้อมสถานะและข้อความ
     */
    public PaymentResponseDTO(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * ระบุผลการชำระเงินสำเร็จหรือไม่
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * กำหนดผลการชำระเงินสำเร็จหรือไม่
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * คืนข้อความแจ้งผลการชำระเงิน
     */
    public String getMessage() {
        return message;
    }

    /**
     * กำหนดข้อความแจ้งผลการชำระเงิน
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * คืนรหัสออเดอร์ที่สร้างจากการชำระเงิน
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * กำหนดรหัสออเดอร์ที่สร้างจากการชำระเงิน
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * คืนเนื้อหาใบเสร็จในรูปแบบ HTML
     */
    public String getReceiptHtml() {
        return receiptHtml;
    }

    /**
     * กำหนดเนื้อหาใบเสร็จในรูปแบบ HTML
     */
    public void setReceiptHtml(String receiptHtml) {
        this.receiptHtml = receiptHtml;
    }

    /**
     * คืนภาพ QR code ในรูปแบบ Base64
     */
    public String getQrCodeImage() {
        return qrCodeImage;
    }

    /**
     * กำหนดภาพ QR code ในรูปแบบ Base64
     */
    public void setQrCodeImage(String qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    /**
     * คืนเงินทอนสำหรับการชำระเงินสด
     */
    public Double getChangeAmount() {
        return changeAmount;
    }

    /**
     * กำหนดเงินทอนสำหรับการชำระเงินสด
     */
    public void setChangeAmount(Double changeAmount) {
        this.changeAmount = changeAmount;
    }

    /**
     * คืนข้อความข้อผิดพลาดในการชำระเงิน
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * กำหนดข้อความข้อผิดพลาดในการชำระเงิน
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
