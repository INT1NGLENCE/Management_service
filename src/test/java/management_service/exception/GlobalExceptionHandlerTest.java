package management_service.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import management.system.dto.ErrorDto;
import management.system.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.security.SignatureException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleEntityNotFoundException() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleEntityNotFoundException(exception);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Entity not found", response.getBody().getMessage());
    }

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleIllegalArgumentException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Illegal argument", response.getBody().getMessage());
    }

    @Test
    public void testHandleException() {
        Exception exception = new Exception("General exception");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("General exception", response.getBody().getMessage());
    }

    @Test
    public void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Runtime exception");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleRuntimeException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Runtime exception", response.getBody().getMessage());
    }

    @Test
    public void testHandleNullPointerException() {
        NullPointerException exception = new NullPointerException("Null pointer exception");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleNullPointerException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Null pointer exception", response.getBody().getMessage());
    }

    @Test
    public void testHandleIOException() {
        IOException exception = new IOException("IO exception");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleIOException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("IO exception", response.getBody().getMessage());
    }

    @Test
    public void testHandleServletException() {
        ServletException exception = new ServletException("Servlet exception");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleServletException(exception);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Servlet exception", response.getBody().getMessage());
    }

    @Test
    public void testHandleInvalidTokenException() {
        SignatureException exception = new SignatureException("Invalid JWT signature");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleInvalidTokenException(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid JWT signature.", response.getBody().getMessage());
    }

    @Test
    public void testHandleExpiredTokenException() {
        ExpiredJwtException exception = new ExpiredJwtException(null, null, "Expired JWT token");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleExpiredTokenException(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Expired JWT token.", response.getBody().getMessage());
    }

    @Test
    public void testHandleMalformedJwtException() {
        MalformedJwtException exception = new MalformedJwtException("Malformed JWT token");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleExpiredTokenException(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Expired JWT token.", response.getBody().getMessage());
    }

    @Test
    public void testHandleUnsupportedTokenException() {
        UnsupportedJwtException exception = new UnsupportedJwtException("Unsupported JWT token");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleUnsupportedTokenException(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unsupported JWT token.", response.getBody().getMessage());
    }

    @Test
    public void testHandleUsernameNotFoundException() {
        UsernameNotFoundException exception = new UsernameNotFoundException("Username not found");
        ResponseEntity<ErrorDto> response = globalExceptionHandler.handleUsernameNotFoundException(exception);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Username not found.", response.getBody().getMessage());
    }
}