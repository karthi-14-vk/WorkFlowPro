
package com.workflow.service;

import com.workflow.dto.EmployeeRegistrationRequest;
import com.workflow.entity.Employee;
import com.workflow.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Employee registerEmployee(EmployeeRegistrationRequest request) {

        // 1. Check whether email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // 2. Create Employee entity
        Employee employee = new Employee();

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());

        // 3. Hash the password
        employee.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        // 4. Assign default role
        employee.setRole("EMPLOYEE");

        // 5. Save employee in MySQL
        return employeeRepository.save(employee);
    }
}
