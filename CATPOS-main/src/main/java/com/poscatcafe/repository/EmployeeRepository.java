package com.poscatcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poscatcafe.model.Employee;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUsername(String username);
}
