package com.poscatcafe.controller;

import com.poscatcafe.model.Product;
import com.poscatcafe.service.ProductService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * จัดการข้อมูลสินค้า เช่น รายการสินค้า เพิ่ม แก้ไข และลบ
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    /**
     * สร้างคอนโทรลเลอร์เพื่อเชื่อมกับบริการจัดการสินค้า
     */
    public ProductController(ProductService service) {
        this.service = service;
    }

    /**
     * ดึงรายการสินค้าทั้งหมดจากระบบ
     */
    @GetMapping
    public List<Product> all() {
        return service.all();
    }

    /**
     * ดึงข้อมูลสินค้าตามรหัสที่ระบุ
     */
    @GetMapping("/{id}")
    public Product get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * เพิ่มสินค้าใหม่เข้าสู่ระบบ
     */
    @PostMapping
    public Product create(@Valid @RequestBody Product product) {
        return service.save(product);
    }

    /**
     * แก้ไขข้อมูลสินค้าตามรหัสที่ระบุ
     */
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @Valid @RequestBody Product product) {
        product.setId(id);
        return service.save(product);
    }

    /**
     * ลบสินค้าตามรหัสออกจากระบบ
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
