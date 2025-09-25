package com.poscatcafe.service;

import com.poscatcafe.dto.LoyaltyDTO;
import com.poscatcafe.model.Customer;
import com.poscatcafe.repository.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * จัดการตรรกะระบบสะสมแต้มและคูปองของลูกค้า
 */
@Service
@Transactional
public class LoyaltyService {
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    // Earn rule: every 10 baht spent grants 1 point
    public static final double POINTS_PER_BAHT = 1.0 / 2.5;

    // Redeem rule: 100 points can be exchanged for 1 discount coupon
    public static final int POINTS_PER_COUPON = 100;

    /**
     * สร้างบริการสะสมแต้มพร้อมพึ่งพาบริการลูกค้าและคลังข้อมูลลูกค้า
     */
    public LoyaltyService(CustomerService customerService, CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.customerRepository = customerRepository;
    }

    /**
     * ค้นหาลูกค้าจากชื่อหรือเบอร์โทรเพื่อดูสถานะสะสมแต้ม
     */
    public List<LoyaltyDTO> searchCustomers(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim().toLowerCase();

        List<Customer> customers = customerService.all().stream()
                .filter(c -> {
                    String name = Optional.ofNullable(c.getName()).orElse("");
                    String phone = Optional.ofNullable(c.getPhone()).orElse("");
                    return name.toLowerCase().contains(normalizedQuery) || phone.contains(query.trim());
                })
                .limit(10)
                .collect(Collectors.toList());

        return customers.stream()
                .map(c -> new LoyaltyDTO(c.getId(), Optional.ofNullable(c.getName()).orElse("ลูกค้า"), c.getLoyaltyPoints()))
                .collect(Collectors.toList());
    }

    /**
     * เพิ่มแต้มให้ลูกค้าตามจำนวนที่ส่งมา
     */
    @Transactional
    public void addPoints(Long customerId, Integer points) {
        Customer customer = customerService.get(customerId);
        if (customer != null) {
            int current = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            customer.setLoyaltyPoints(current + (points != null ? points : 0));
            customerService.save(customer);
        }
    }

    /**
     * คำนวณแต้มที่จะได้รับจากยอดซื้อที่กำหนด
     */
    public int calculateEarnedPoints(double purchaseAmount) {
        return (int) Math.floor(purchaseAmount * POINTS_PER_BAHT);
    }

    /**
     * คำนวณและเพิ่มแต้มสะสมจากการซื้อ
     */
    public Customer addPointsFromPurchase(Long customerId, double purchaseAmount) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            int current = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            int pointsToAdd = calculateEarnedPoints(purchaseAmount);
            customer.setLoyaltyPoints(current + pointsToAdd);
            return customerRepository.save(customer);
        }
        return null;
    }

    /**
     * แลกแต้มเป็นคูปองส่วนลดหนึ่งใบ
     */
    @Transactional
    public boolean redeemCoupon(Long customerId) {
        Customer customer = customerService.get(customerId);
        if (customer != null) {
            int current = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            if (current >= POINTS_PER_COUPON) {
                customer.setLoyaltyPoints(current - POINTS_PER_COUPON);
                customerService.save(customer);
                return true;
            }
        }
        return false;
    }

    /**
     * ใช้แต้มแลกคูปอง 1 ใบ (เทียบเท่าลดราคาสินค้าราคาต่ำสุด 1 รายการ) ระดับ service ใช้ภายใน PaymentService
     */
    @Transactional
    public boolean useCoupon(Long customerId) {
        return redeemCoupon(customerId);
    }

    /**
     * ข้อมูลแต้มสะสมแบบละเอียด
     */
    public LoyaltyDetailDTO getLoyaltyDetail(Long customerId) {
        Customer customer = customerService.get(customerId);
        if (customer != null) {
            int totalPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            int availableCoupons = totalPoints / POINTS_PER_COUPON;
            int remainingPoints = totalPoints % POINTS_PER_COUPON;
            int pointsNeeded = remainingPoints == 0 ? POINTS_PER_COUPON : POINTS_PER_COUPON - remainingPoints;

            return new LoyaltyDetailDTO(
                customer.getId(),
                customer.getName(),
                totalPoints,
                availableCoupons,
                remainingPoints,
                pointsNeeded
            );
        }
        return null;
    }

    /**
     * DTO สำหรับข้อมูลแต้มสะสมแบบละเอียด
     */
    public static class LoyaltyDetailDTO {
        private Long customerId;
        private String customerName;
        private int totalPoints;
        private int availableFreeDrinks;
        private int remainingPoints;
        private int pointsNeededForNextDrink;

        /**
         * สร้างข้อมูลแต้มสะสมแบบละเอียดพร้อมค่าทั้งหมด
         */
        public LoyaltyDetailDTO(Long customerId, String customerName, int totalPoints,
                int availableFreeDrinks, int remainingPoints, int pointsNeededForNextDrink) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.totalPoints = totalPoints;
            this.availableFreeDrinks = availableFreeDrinks;
            this.remainingPoints = remainingPoints;
            this.pointsNeededForNextDrink = pointsNeededForNextDrink;
        }

        /**
         * คืนรหัสลูกค้า
         */
        public Long getCustomerId() {
            return customerId;
        }

        /**
         * คืนชื่อลูกค้า
         */
        public String getCustomerName() {
            return customerName;
        }

        /**
         * คืนจำนวนแต้มสะสมทั้งหมด
         */
        public int getTotalPoints() {
            return totalPoints;
        }

        /**
         * คืนจำนวนเครื่องดื่มฟรีที่แลกได้
         */
        public int getAvailableFreeDrinks() {
            return availableFreeDrinks;
        }

        /**
         * คืนจำนวนแต้มที่เหลือหลังหักส่วนที่แลกได้แล้ว
         */
        public int getRemainingPoints() {
            return remainingPoints;
        }

        /**
         * คืนจำนวนแต้มที่ต้องสะสมเพิ่มเพื่อแลกเครื่องดื่มถัดไป
         */
        public int getPointsNeededForNextDrink() {
            return pointsNeededForNextDrink;
        }
    }
}
