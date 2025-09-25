package com.poscatcafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * คลาสหลักสำหรับบูตระบบ POS ของร้านคาเฟ่แมวด้วย Spring Boot
 */
@SpringBootApplication
public class CatCafePosApplication {

  /**
   * เมธอดหลักสำหรับเริ่มการทำงานของ Spring Boot application
   */
  public static void main(String[] args) {
    SpringApplication.run(CatCafePosApplication.class, args);
  }
}
