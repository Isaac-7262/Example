package com.poscatcafe.service;

import com.poscatcafe.model.Product;
import com.poscatcafe.repository.ProductRepository;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ให้บริการจัดการข้อมูลสินค้า
 */
@Service
public class ProductService {
    private final ProductRepository repo;

    /**
     * สร้างบริการสินค้าเชื่อมกับคลังข้อมูล
     */
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    /**
     * ดึงสินค้าทั้งหมด
     */
    public List<Product> all() {
        return repo.findAll();
    }

    /**
     * ดึงสินค้าตามรหัส
     */
    public Product get(Long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * บันทึกหรืออัปเดตข้อมูลสินค้า
     */
    public Product save(Product product) {
        return repo.save(product);
    }

    /**
     * ลบสินค้าตามรหัส
     */
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
