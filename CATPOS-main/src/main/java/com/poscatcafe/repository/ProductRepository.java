package com.poscatcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poscatcafe.model.Product;

/**
 * จัดการข้อมูลสินค้าบนฐานข้อมูล
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * ค้นหาสินค้าจากรหัสสินค้า
     */
    Product findByCode(String code);
}
