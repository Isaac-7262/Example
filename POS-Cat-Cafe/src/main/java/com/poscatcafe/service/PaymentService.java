package com.poscatcafe.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.poscatcafe.dto.*;
import com.poscatcafe.model.*;
import com.google.zxing.common.BitMatrix;
import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * จัดการกระบวนการชำระเงินครบวงจร รวมถึงออกใบเสร็จและจัดการสต็อก
 */
@Service
public class PaymentService {
    private final CartService cartService;
    private final OrderService orderService;
    private final ProductService productService;
    private final LoyaltyService loyaltyService;
    private final CustomerService customerService;
    private final ReceiptService receiptService;
    private final StoreSettingService storeSettingService;

    /**
     * สร้างบริการชำระเงินพร้อมบริการอื่นที่เกี่ยวข้อง
     */
    public PaymentService(
            CartService cartService,
            OrderService orderService,
            ProductService productService,
            LoyaltyService loyaltyService,
            CustomerService customerService,
            ReceiptService receiptService,
            StoreSettingService storeSettingService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.productService = productService;
        this.loyaltyService = loyaltyService;
        this.customerService = customerService;
        this.receiptService = receiptService;
        this.storeSettingService = storeSettingService;
    }

    /**
     * ประมวลผลการชำระเงินตั้งแต่คำนวณยอด ตรวจสอบสต็อก ไปจนถึงออกใบเสร็จ
     */
    @Transactional
    public PaymentResponseDTO processPayment(PaymentRequestDTO request) {
        PaymentResponseDTO response = new PaymentResponseDTO();

        try {
            // 1. Calculate cart and validate stock
            CartCalculationDTO cart = cartService.calculateCart(
                    request.getItems(), request.getCustomerId(), request.getUseCoupon());

            if (!cart.getStockAvailable()) {
                response.setSuccess(false);
                response.setErrorMessage(cart.getErrorMessage());
                return response;
            }

            // 2. Generate QR code for QR payments
            if ("qr".equals(request.getPaymentMethod())) {
                String qrCodeImage = generateQRCode(cart.getTotal());
                response.setQrCodeImage(qrCodeImage);
            }

            // 3. Calculate change for cash payments
            if ("cash".equals(request.getPaymentMethod()) && request.getCashReceived() != null) {
                double change = request.getCashReceived() - cart.getTotal();
                if (change < 0) {
                    response.setSuccess(false);
                    response.setErrorMessage("เงินที่รับไม่เพียงพอ");
                    return response;
                }
                response.setChangeAmount(change);
            }

            // 4. Create order
            Order order = createOrder(cart, request);
            order = orderService.save(order);

            // 5. Handle loyalty operations
            if (request.getCustomerId() != null) {
                if (Boolean.TRUE.equals(request.getUseCoupon())) {
                    boolean redeemed = loyaltyService.useCoupon(request.getCustomerId());
                    if (!redeemed) {
                        throw new IllegalStateException("แต้มไม่เพียงพอสำหรับใช้คูปอง");
                    }
                }
                loyaltyService.addPointsFromPurchase(request.getCustomerId(), order.getTotalAmount());
            }

            // 6. Generate receipt
            String receiptHtml = receiptService.generateReceiptHtml(order);

            response.setSuccess(true);
            response.setMessage("ชำระเงินสำเร็จ");
            response.setOrderId(order.getId());
            response.setReceiptHtml(receiptHtml);

        } catch (Exception e) {
            response.setSuccess(false);
            response.setErrorMessage("เกิดข้อผิดพลาด: " + e.getMessage());
        }

        return response;
    }

    /**
     * สร้างภาพ QR code สำหรับการชำระเงินผ่านพร้อมเพย์
     */
    private String generateQRCode(Double amount) {
        try {
            StoreSetting settings = storeSettingService.get();
            String promptPayId = settings.getPromptpayId();

            if (promptPayId == null || promptPayId.isEmpty()) {
                promptPayId = "0123456789"; // Default PromptPay ID
            }

            // PromptPay QR Code format
            String qrData = String.format("00020101021229370016A000000677010111011%s5303764540%.2f6304",
                    promptPayId, amount);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 200, 200, hints);

            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * สร้างออเดอร์จากผลการคำนวณตะกร้าและคำขอชำระเงิน
     */
    private Order createOrder(CartCalculationDTO cart, PaymentRequestDTO request) {
        Order order = new Order();
        order.setTotalAmount(cart.getTotal());
        order.setDiscountAmount(cart.getDiscount());
        order.setPaymentMethod(request.getPaymentMethod());

        if (request.getCustomerId() != null) {
            Customer customer = customerService.get(request.getCustomerId());
            order.setCustomer(customer);
            order.setCustomerName(customer.getName());
        }

        // Create order items
        for (CartItemDTO cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            Product product = productService.get(cartItem.getProductId());
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setSubtotal(cartItem.getSubtotal());
            order.getItems().add(orderItem);
        }

        return order;
    }

}
