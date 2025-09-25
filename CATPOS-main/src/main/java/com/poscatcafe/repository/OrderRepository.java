package com.poscatcafe.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poscatcafe.model.Order;

/**
 * ใช้เข้าถึงและค้นหาออเดอร์ในฐานข้อมูล
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * ค้นหาออเดอร์ภายในช่วงเวลาที่กำหนด
     */
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
}
