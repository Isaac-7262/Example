package com.poscatcafe.service;

import org.springframework.stereotype.Service;

import com.poscatcafe.model.Order;
import com.poscatcafe.model.OrderItem;
import com.poscatcafe.model.StoreSetting;

import java.time.format.DateTimeFormatter;

/**
 * สร้างใบเสร็จ HTML สำหรับการชำระเงิน
 */
@Service
public class ReceiptService {
    private final StoreSettingService storeSettingService;

    /**
     * สร้างบริการใบเสร็จพร้อมเข้าถึงข้อมูลการตั้งค่าร้าน
     */
    public ReceiptService(StoreSettingService storeSettingService) {
        this.storeSettingService = storeSettingService;
    }

    /**
     * สร้างเนื้อหาใบเสร็จในรูปแบบ HTML ตามข้อมูลออเดอร์
     */
    public String generateReceiptHtml(Order order) {
        StoreSetting settings = storeSettingService.get();
        String shopName = settings.getShopName() != null ? settings.getShopName() : "🐱 Cat Café POS";
        String shopAddress = settings.getAddress() != null ? settings.getAddress() : "123 Cat Street";
        String shopPhone = settings.getPhone() != null ? settings.getPhone() : "02-123-4567";
        String taxId = settings.getTaxId() != null ? settings.getTaxId() : "1234567890123";

        StringBuilder html = new StringBuilder();
        html.append("<div class='receipt'>");

        // Header
        html.append("<div class='receipt-header'>");
        html.append("<div class='receipt-title'>").append(shopName).append("</div>");
        html.append("<div>").append(shopAddress).append("</div>");
        html.append("<div>โทร: ").append(shopPhone).append("</div>");
        html.append("<div>เลขประจำตัวผู้เสียภาษี: ").append(taxId).append("</div>");
        html.append("</div>");

        // Order details
        html.append("<div style='text-align: center; margin: 15px 0;'>");
        html.append("<div><strong>ใบเสร็จรับเงิน</strong></div>");
        html.append("<div>เลขที่: ").append(String.format("R%06d", order.getId())).append("</div>");
        html.append("<div>วันที่: ").append(order.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</div>");
        if (order.getCustomerName() != null && !order.getCustomerName().isEmpty()) {
            html.append("<div>ลูกค้า: ").append(order.getCustomerName()).append("</div>");
        }
        html.append("</div>");

        // Items
        html.append("<div style='border-top: 1px dashed #666; padding-top: 10px;'>");
        double subtotal = 0.0;
        for (OrderItem item : order.getItems()) {
            html.append("<div class='receipt-item'>");
            html.append("<span>").append(item.getProductName()).append("</span>");
            html.append("<span>").append(String.format("%.2f", item.getSubtotal())).append("</span>");
            html.append("</div>");
            html.append("<div class='receipt-item-details'>");
            html.append("จำนวน ").append(item.getQuantity()).append(" x ").append(String.format("%.2f", item.getPrice()));
            html.append("</div>");
            subtotal += item.getSubtotal();
        }
        html.append("</div>");

        // Totals
        html.append("<div class='receipt-total'>");
        html.append("<div class='receipt-item'>");
        html.append("<span>รวมย่อย:</span>");
        html.append("<span>฿").append(String.format("%.2f", subtotal)).append("</span>");
        html.append("</div>");

        if (order.getDiscountAmount() != null && order.getDiscountAmount() > 0) {
            html.append("<div class='receipt-item'>");
            html.append("<span>ส่วนลด:</span>");
            html.append("<span>-฿").append(String.format("%.2f", order.getDiscountAmount())).append("</span>");
            html.append("</div>");
        }

        html.append("<div class='receipt-item' style='font-size: 1.2em; font-weight: bold;'>");
        html.append("<span>รวมทั้งสิ้น:</span>");
        html.append("<span>฿").append(String.format("%.2f", order.getTotalAmount())).append("</span>");
        html.append("</div>");
        html.append("</div>");

        // Footer
        html.append("<div style='text-align: center; margin-top: 15px; border-top: 1px dashed #666; padding-top: 10px;'>");
        html.append("<div>*** ขอบคุณที่ใช้บริการ ***</div>");
        html.append("<div>🐱 Cat Café POS System</div>");
        html.append("</div>");

        html.append("</div>");

        return html.toString();
    }
}
