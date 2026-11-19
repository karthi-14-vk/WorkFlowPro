
package com.workflow.service;

import com.workflow.dto.EmployeeLoginRequest;
import com.workflow.dto.EmployeeRegistrationRequest;
import com.workflow.dto.LoginResponse;
import com.workflow.entity.Employee;
import com.workflow.repository.EmployeeRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void shouldRegisterEmployeeSuccessfully() {

        // Arrange: Prepare test data
        EmployeeRegistrationRequest request =
                new EmployeeRegistrationRequest();

        request.setFullName("Test Employee");
        request.setEmail("test@example.com");
        request.setPassword("Test@12345");

        Employee savedEmployee = new Employee();
        savedEmployee.setFullName("Test Employee");
        savedEmployee.setEmail("test@example.com");
        savedEmployee.setPasswordHash("encodedPassword");
        savedEmployee.setRole("EMPLOYEE");

        when(employeeRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Test@12345"))
                .thenReturn("encodedPassword");

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        // Act: Execute the actual service method
        Employee result = employeeService.registerEmployee(request);

        // Assert: Verify the result
        assertNotNull(result);
        assertEquals("Test Employee", result.getFullName());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("encodedPassword", result.getPasswordHash());

        verify(employeeRepository).save(any(Employee.class));
        verify(passwordEncoder).encode("Test@12345");
    }


    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange
        EmployeeRegistrationRequest request =
                new EmployeeRegistrationRequest();

        request.setFullName("Test Employee");
        request.setEmail("existing@example.com");
        request.setPassword("Test@12345");

        when(employeeRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.registerEmployee(request)
        );

        // Verify that employee was never saved
        verify(employeeRepository, never()).save(any(Employee.class));

        // Verify that password was never encoded
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldLoginSuccessfullyAndGenerateJwtToken() {

        // Arrange: Prepare employee data
        Employee employee = new Employee();

        employee.setFullName("Test Employee");
        employee.setEmail("test@example.com");
        employee.setPasswordHash("encodedPassword");
        employee.setRole("EMPLOYEE");

        EmployeeLoginRequest request = new EmployeeLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test@12345");

        // Mock repository response
        when(employeeRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(employee));

        // Mock password verification
        when(passwordEncoder.matches(
                "Test@12345",
                "encodedPassword"
        )).thenReturn(true);

        // Mock JWT generation
        when(jwtService.generateToken("test@example.com"))
                .thenReturn("mock-jwt-token");

        // Act: Execute login
        LoginResponse response = employeeService.login(request);

        // Assert: Verify response
        assertNotNull(response);

        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400, response.getExpiresIn());

        assertEquals("Test Employee", response.getFullName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("EMPLOYEE", response.getRole());

        // Verify method calls
        verify(employeeRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches(
                "Test@12345",
                "encodedPassword"
        );
        verify(jwtService).generateToken("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {

        // Arrange
        Employee employee = new Employee();
        employee.setFullName("Test Employee");
        employee.setEmail("test@example.com");
        employee.setPasswordHash("encodedPassword");
        employee.setRole("EMPLOYEE");

        EmployeeLoginRequest request = new EmployeeLoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword");

        when(employeeRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(employee));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        // Act and Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> employeeService.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        // JWT must not be generated
        verify(jwtService, never()).generateToken(anyString());
    }


    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        // Arrange
        EmployeeLoginRequest request = new EmployeeLoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("Test@12345");

        when(employeeRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // Act and Assert
        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> employeeService.login(request)
        );

        assertEquals(
                "Invalid email or password",
                exception.getMessage()
        );

        // Password verification and JWT generation must not happen
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }


}
