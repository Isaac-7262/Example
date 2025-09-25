package com.poscatcafe.dto;

/**
 * ตัวแทนสินค้าแต่ละรายการในตะกร้า พร้อมข้อมูลที่จำเป็นสำหรับคำนวณราคา
 */
public class CartItemDTO {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double subtotal;

    /**
     * สร้างรายการสินค้าในตะกร้าแบบเปล่า
     */
    public CartItemDTO() {}

    /**
     * สร้างรายการสินค้าในตะกร้าพร้อมข้อมูลเริ่มต้น
     */
    public CartItemDTO(Long productId, String productName, String imageUrl, Double price, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }

    /**
     * คืนรหัสสินค้าในตะกร้า
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * กำหนดรหัสสินค้าในตะกร้า
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * คืนชื่อสินค้าในตะกร้า
     */
    public String getProductName() {
        return productName;
    }

    /**
     * กำหนดชื่อสินค้าในตะกร้า
     */
    public void setProductName(String productName) {
        this.productName = productName;
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

    /**
     * คืนราคาต่อหน่วยของสินค้า
     */
    public Double getPrice() {
        return price;
    }

    /**
     * กำหนดราคาต่อหน่วยและปรับยอดรวมย่อยใหม่
     */
    public void setPrice(Double price) {
        this.price = price;
        this.updateSubtotal();
    }

    /**
     * คืนจำนวนสินค้าที่เลือก
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * กำหนดจำนวนสินค้าและปรับยอดรวมย่อยใหม่
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.updateSubtotal();
    }

    /**
     * คืนยอดรวมย่อยของสินค้า
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * กำหนดยอดรวมย่อยของสินค้าโดยตรง
     */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * คำนวณยอดรวมย่อยจากราคาและจำนวนล่าสุด
     */
    private void updateSubtotal() {
        if (price != null && quantity != null) {
            this.subtotal = price * quantity;
        }
    }
}
