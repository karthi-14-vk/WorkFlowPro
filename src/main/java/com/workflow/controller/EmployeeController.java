
package com.workflow.controller;

import com.workflow.dto.EmployeeRegistrationRequest;
import com.workflow.entity.Employee;
import com.workflow.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerEmployee(
            @Valid @RequestBody EmployeeRegistrationRequest request) {

        Employee employee = employeeService.registerEmployee(request);

        Map<String, Object> response = Map.of(
                "message", "Employee registered successfully",
                "employeeId", employee.getId(),
                "fullName", employee.getFullName(),
                "email", employee.getEmail(),
                "role", employee.getRole()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
