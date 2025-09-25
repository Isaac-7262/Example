package com.poscatcafe.controller;

import com.poscatcafe.dto.EmployeeDTO;
import com.poscatcafe.dto.EmployeeRequestDTO;
import com.poscatcafe.model.Employee;
import com.poscatcafe.service.EmployeeService;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.getAllEmployees().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmployeeDTO getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    @PostMapping
    public EmployeeDTO createEmployee(@RequestBody EmployeeRequestDTO request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setUsername(request.getUsername());
        employee.setPassword(request.getPassword());
        employee.setRole(request.getRole());
        return toDTO(employeeService.createEmployee(employee));
    }

    @PutMapping("/{id}")
    public EmployeeDTO updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO request) {
        Employee updated = new Employee();
        updated.setName(request.getName());
        updated.setUsername(request.getUsername());
        updated.setPassword(request.getPassword());
        updated.setRole(request.getRole());
        Employee result = employeeService.updateEmployee(id, updated);
        return result != null ? toDTO(result) : null;
    }

    @DeleteMapping("/{id}")
    public boolean deleteEmployee(@PathVariable Long id) {
        return employeeService.deleteEmployee(id);
    }

    private EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setUsername(employee.getUsername());
        dto.setRole(employee.getRole());
        return dto;
    }
}