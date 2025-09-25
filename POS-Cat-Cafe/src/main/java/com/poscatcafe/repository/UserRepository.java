package com.poscatcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poscatcafe.model.User;

import java.util.Optional;

/**
 * เข้าถึงและจัดการข้อมูลพนักงานในฐานข้อมูล
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ค้นหาพนักงานโดยใช้ชื่อบัญชี
     */
    Optional<User> findByUsername(String username);

    /**
     * ค้นหาพนักงานด้วยชื่อบัญชีและสถานะการใช้งาน
     */
    Optional<User> findByUsernameAndActive(String username, Boolean active);
}
