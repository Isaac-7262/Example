package com.poscatcafe.controller;

import com.poscatcafe.dto.CartCalculationDTO;
import com.poscatcafe.dto.CartItemDTO;
import com.poscatcafe.service.CartService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * จัดการคำขอเกี่ยวกับตะกร้าสินค้าและการคำนวณยอดเงิน
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    /**
     * สร้างคอนโทรลเลอร์พร้อมบริการคำนวณตะกร้าสินค้า
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * รับรายการสินค้าในตะกร้าและคำนวณยอดรวมพร้อมส่วนลดและแต้มสะสม
     */
    @PostMapping("/calculate")
    public CartCalculationDTO calculateCart(
            @RequestBody List<CartItemDTO> items,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false, defaultValue = "false") Boolean useCoupon) {
        return cartService.calculateCart(items, customerId, useCoupon);
    }
}
