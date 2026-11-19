
package com.workflow.service;

import com.workflow.dto.EmployeeProfileResponse;
import com.workflow.dto.EmployeeRegistrationRequest;
import com.workflow.dto.EmployeeLoginRequest;
import com.workflow.dto.LoginResponse;
import com.workflow.entity.Employee;
import com.workflow.repository.EmployeeRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Employee registerEmployee(EmployeeRegistrationRequest request) {

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Employee employee = new Employee();

        employee.setFullName(request.getFullName());
        employee.setEmail(request.getEmail());

        employee.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        employee.setRole("EMPLOYEE");

        return employeeRepository.save(employee);
    }

    public LoginResponse login(EmployeeLoginRequest request) {

        Employee employee = employeeRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                employee.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

        // Generate JWT using the injected JwtService
        String token = jwtService.generateToken(employee.getEmail());

        return new LoginResponse(
                token,
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getRole()
        );
    }

    public EmployeeProfileResponse getEmployeeProfile(String email) {

        Employee employee = employeeRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Employee not found")
                );

        return new EmployeeProfileResponse(
                employee.getId(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getRole()
        );
    }

}
