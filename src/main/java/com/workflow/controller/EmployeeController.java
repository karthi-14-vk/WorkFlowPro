
package com.workflow.controller;

import com.workflow.dto.EmployeeProfileResponse;
import com.workflow.dto.EmployeeRegistrationRequest;
import com.workflow.entity.Employee;
import com.workflow.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.workflow.dto.EmployeeLoginRequest;
import com.workflow.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody EmployeeLoginRequest request) {

        LoginResponse response = employeeService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeProfileResponse> getMyProfile(
            org.springframework.security.core.Authentication authentication) {

        String email = authentication.getName();

        EmployeeProfileResponse response =
                employeeService.getEmployeeProfile(email);

        return ResponseEntity.ok(response);
    }

}
