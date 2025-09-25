package com.poscatcafe.service;

import com.poscatcafe.model.Customer;
import com.poscatcafe.repository.CustomerRepository;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * ให้บริการจัดการข้อมูลลูกค้า
 */
@Service
public class CustomerService {
    private final CustomerRepository repo;

    /**
     * สร้างบริการลูกค้าพร้อมคลังข้อมูล
     */
    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    /**
     * ดึงรายชื่อลูกค้าทั้งหมด
     */
    public List<Customer> all() {
        return repo.findAll();
    }

    /**
     * ดึงข้อมูลลูกค้าตามรหัส
     */
    public Customer get(Long id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * บันทึกหรืออัปเดตข้อมูลลูกค้า
     */
    public Customer save(Customer customer) {
        return repo.save(customer);
    }

    /**
     * ลบข้อมูลลูกค้าตามรหัส
     */
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
