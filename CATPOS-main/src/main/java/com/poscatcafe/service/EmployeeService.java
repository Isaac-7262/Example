package com.poscatcafe.service;

import com.poscatcafe.model.Employee;
import com.poscatcafe.repository.EmployeeRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {
        // เข้ารหัสรหัสผ่านก่อนบันทึก
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setName(updatedEmployee.getName());
                    employee.setUsername(updatedEmployee.getUsername());
                    // ถ้ามีการเปลี่ยนรหัสผ่าน ให้เข้ารหัสใหม่
                    if (updatedEmployee.getPassword() != null && !updatedEmployee.getPassword().isEmpty()) {
                        employee.setPassword(passwordEncoder.encode(updatedEmployee.getPassword()));
                    }
                    employee.setRole(updatedEmployee.getRole());
                    return employeeRepository.save(employee);
                })
                .orElse(null);
    }

    public boolean deleteEmployee(Long id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Employee> findByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }
}