
package com.workflow.security;

import com.workflow.service.JwtService;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateRequestWithValidToken() throws Exception {

        // Arrange
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        request.addHeader(
                "Authorization",
                "Bearer valid-test-token"
        );

        when(jwtService.extractEmail("valid-test-token"))
                .thenReturn("test@example.com");

        when(jwtService.isTokenValid(
                "valid-test-token",
                "test@example.com"
        )).thenReturn(true);

        // Act
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert
        assertNotNull(
                SecurityContextHolder.getContext().getAuthentication()
        );

        assertEquals(
                "test@example.com",
                SecurityContextHolder.getContext()
                        .getAuthentication().getName()
        );

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {

        // Arrange
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        request.addHeader(
                "Authorization",
                "Bearer invalid-test-token"
        );

        when(jwtService.extractEmail("invalid-test-token"))
                .thenThrow(new io.jsonwebtoken.JwtException(
                        "Invalid token"
                ));

        // Act
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert
        assertEquals(401, response.getStatus());

        assertTrue(
                response.getContentAsString()
                        .contains("Invalid or expired token")
        );

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing()
            throws Exception {

        // Arrange
        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        // Act
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // Assert
        verify(filterChain).doFilter(request, response);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }
}
