
package com.workflow.service;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET =
            Base64.getEncoder().encodeToString(
                    "0123456789abcdef0123456789abcdef"
                            .getBytes(StandardCharsets.UTF_8)
            );

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 86400000);
    }

    @Test
    void shouldGenerateValidJwtToken() {

        // Arrange
        String email = "test@example.com";

        // Act
        String token = jwtService.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());

        assertEquals(email, jwtService.extractEmail(token));

        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    void shouldRejectTokenSignedWithDifferentSecret() {

        // Arrange
        String token = jwtService.generateToken("test@example.com");

        String differentSecret =
                Base64.getEncoder().encodeToString(
                        "abcdef0123456789abcdef0123456789"
                                .getBytes(StandardCharsets.UTF_8)
                );

        JwtService anotherJwtService =
                new JwtService(differentSecret, 86400000);

        // Act and Assert
        assertThrows(
                JwtException.class,
                () -> anotherJwtService.extractEmail(token)
        );
    }
}
