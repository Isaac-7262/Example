package com.poscatcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poscatcafe.model.Customer;

/**
 * จัดการข้อมูลลูกค้าในฐานข้อมูล
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {}
