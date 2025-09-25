package com.poscatcafe.service;

import com.poscatcafe.model.Order;
import com.poscatcafe.repository.OrderRepository;
import com.poscatcafe.repository.ProductRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ให้บริการเกี่ยวกับออเดอร์ เช่น บันทึก สรุปยอด และสร้างรายงาน
 */
@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    /**
     * สร้างบริการออเดอร์พร้อมพึ่งพาคลังข้อมูลและบริการแต้มสะสม
     */
    public OrderService(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    /**
     * บันทึกออเดอร์ใหม่ พร้อมปรับสต็อกและเพิ่มแต้มสะสมให้ลูกค้า
     */
    public Order save(Order order) {
        double subtotal = 0;
        for (var item : order.getItems()) {
            var product = productRepo.findById(item.getProduct().getId()).orElseThrow();
            item.setProduct(product);
            if (item.getPrice() <= 0) {
                item.setPrice(product.getPrice());
            }
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock not enough for " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepo.save(product);
            subtotal += item.getPrice() * item.getQuantity();
        }
        double discount = Math.max(0.0, order.getDiscountAmount() != null ? order.getDiscountAmount() : 0.0);
        double total = Math.max(0.0, subtotal - discount);
        order.setDiscountAmount(discount);
        order.setTotalAmount(total);
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        if (order.getReceiptNo() == null || order.getReceiptNo().isBlank()) {
            order.setReceiptNo(
                    "R" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        }
        return orderRepo.save(order);
    }

    /**
     * ดึงออเดอร์ในช่วงวันที่กำหนด
     */
    public List<Order> between(LocalDate start, LocalDate end) {
        return orderRepo.findByOrderDateBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

}
