package management_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import management.system.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String secretKey = "bXlzZWNyZXRrZXlteXNlY3JldGtleW15c2VjcmV0a2V5bXlzZWNyZXRrZXk=";

    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secretKey);
    }

    @Test
    public void testGenerateToken() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    public void testGenerateToken_NullAuthentication() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.generateToken(null);
        });
        assertEquals("Authentication cannot be null or unauthenticated", exception.getMessage());
    }

    @Test
    public void testGetUserEmailFromJWT() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtTokenProvider.generateToken(authentication);
        String email = jwtTokenProvider.getUserEmailFromJWT(token);

        assertEquals("testuser", email);
    }

    @Test
    public void testValidateToken() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtTokenProvider.generateToken(authentication);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_Expired() {
        SecretKey key = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secretKey));
        String token = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 36000000)) // 10 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 18000000)) // 5 hours ago
                .signWith(key)
                .compact();

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    public void testParseClaims() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtTokenProvider.generateToken(authentication);
        Claims claims = jwtTokenProvider.parseClaims(token);

        assertNotNull(claims);
        assertEquals("testuser", claims.getSubject());
    }

    @Test
    public void testParseClaims_InvalidToken() {
        String invalidToken = "invalid.token.here";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.parseClaims(invalidToken);
        });
        assertEquals("Failed to parse token", exception.getMessage());
    }
}