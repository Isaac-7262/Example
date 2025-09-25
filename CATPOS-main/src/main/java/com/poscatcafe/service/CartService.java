package com.poscatcafe.service;

import com.poscatcafe.dto.CartCalculationDTO;
import com.poscatcafe.dto.CartItemDTO;
import com.poscatcafe.model.Customer;
import com.poscatcafe.model.Product;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * คำนวณยอดตะกร้าสินค้าและตรวจสอบสต็อก
 */
@Service
public class CartService {
    private final ProductService productService;
    private final CustomerService customerService;
    private final LoyaltyService loyaltyService;

    /**
     * สร้างบริการตะกร้าพร้อมเข้าถึงข้อมูลสินค้าและลูกค้า
     */
    public CartService(
            ProductService productService, CustomerService customerService, LoyaltyService loyaltyService) {
        this.productService = productService;
        this.customerService = customerService;
        this.loyaltyService = loyaltyService;
    }

    /**
     * คำนวณยอดรวม ส่วนลด และแต้มสะสมของตะกร้าสินค้า
     */
    public CartCalculationDTO calculateCart(List<CartItemDTO> items, Long customerId, Boolean useCoupon) {
        CartCalculationDTO result = new CartCalculationDTO();

        try {
            // Validate stock availability
            boolean stockAvailable = validateStock(items);
            result.setStockAvailable(stockAvailable);

            if (!stockAvailable) {
                result.setErrorMessage("สินค้าในตะกร้าบางรายการไม่เพียงพอ");
                return result;
            }

            // Calculate subtotal
            double subtotal = items.stream()
                    .mapToDouble(CartItemDTO::getSubtotal)
                    .sum();
            result.setSubtotal(subtotal);

            // Calculate discount
            double discount = 0.0;
            String discountDescription = "";

            if (customerId != null && useCoupon != null && useCoupon) {
                Customer customer = customerService.get(customerId);
                if (customer != null) {
                    int points = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
                    if (points >= LoyaltyService.POINTS_PER_COUPON) {
                        // Use 1 coupon (100 points) for free cheapest item
                        double cheapestItemPrice = items.stream()
                                .mapToDouble(CartItemDTO::getPrice)
                                .min()
                                .orElse(0.0);
                        discount = cheapestItemPrice;
                        discountDescription = "ใช้คูปองฟรี 1 รายการ";
                    }
                }
            }

            result.setDiscount(discount);
            result.setDiscountDescription(discountDescription);

            // Calculate total
            double total = Math.max(0.0, subtotal - discount);
            result.setTotal(total);

            // Calculate loyalty points earned (1 point per 10 baht)
            int pointsEarned = loyaltyService.calculateEarnedPoints(total);
            result.setLoyaltyPointsEarned(pointsEarned);

            result.setItems(items);

        } catch (Exception e) {
            result.setStockAvailable(false);
            result.setErrorMessage("เกิดข้อผิดพลาดในการคำนวณ: " + e.getMessage());
        }

        return result;
    }

    /**
     * ตรวจสอบว่าสินค้าในตะกร้าทุกชิ้นมีสต็อกเพียงพอหรือไม่
     */
    private boolean validateStock(List<CartItemDTO> items) {
        for (CartItemDTO item : items) {
            try {
                Product product = productService.get(item.getProductId());
                if (product == null || product.getStock() < item.getQuantity()) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
