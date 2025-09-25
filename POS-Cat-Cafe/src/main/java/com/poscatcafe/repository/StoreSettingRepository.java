package com.poscatcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poscatcafe.model.StoreSetting;

/**
 * จัดการข้อมูลการตั้งค่าร้านในฐานข้อมูล
 */
public interface StoreSettingRepository extends JpaRepository<StoreSetting, Long> {}
