
package com.workflow.dto;

public class LoginResponse {

    private String token;
    private String tokenType;
    private long expiresIn;
    private Long employeeId;
    private String fullName;
    private String email;
    private String role;

    public LoginResponse(
            String token,
            Long employeeId,
            String fullName,
            String email,
            String role) {

        this.token = token;
        this.tokenType = "Bearer";
        this.expiresIn = 86400;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
