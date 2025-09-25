package com.poscatcafe.controller;

import com.poscatcafe.model.Customer;
import com.poscatcafe.service.CustomerService;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * จัดการคำขอของลูกค้า เช่น ค้นหา เพิ่ม แก้ไข และลบข้อมูลลูกค้า
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    /**
     * สร้างคอนโทรลเลอร์เพื่อเรียกใช้บริการลูกค้า
     */
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /**
     * ดึงรายชื่อลูกค้าทั้งหมด
     */
    @GetMapping
    public List<Customer> all() {
        return service.all();
    }

    /**
     * ดึงข้อมูลลูกค้าตามรหัสที่ระบุ
     */
    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * เพิ่มลูกค้าใหม่ลงในระบบ
     */
    @PostMapping
    public Customer create(@Valid @RequestBody Customer customer) {
        return service.save(customer);
    }

    /**
     * แก้ไขข้อมูลลูกค้าตามรหัส
     */
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        return service.save(customer);
    }

    /**
     * ลบลูกค้าตามรหัสที่ส่งมา
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
