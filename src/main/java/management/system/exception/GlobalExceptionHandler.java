package management.system.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import management.system.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.security.SignatureException;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;
    private static final HttpStatus NOT_FOUND = HttpStatus.NOT_FOUND;
    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorDto> handleNullPointerException(NullPointerException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorDto> handleIOException(IOException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorDto> handleServletException(ServletException exception) {
        return new ResponseEntity<>(createErrorDto(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorDto> handleInvalidTokenException(SignatureException exception) {
        return new ResponseEntity<>(createErrorDto("Invalid JWT signature. " + exception.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorDto> handleExpiredTokenException(MalformedJwtException exception) {
        return new ResponseEntity<>(createErrorDto("Expired JWT token. " + exception.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDto> handleExpiredTokenException(ExpiredJwtException exception) {
        return new ResponseEntity<>(createErrorDto("Expired JWT token. " + exception.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorDto> handleUnsupportedTokenException(UnsupportedJwtException exception) {
        return new ResponseEntity<>(createErrorDto("Unsupported JWT token. " + exception.getMessage()),
                UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return new ResponseEntity<>(createErrorDto("Username not found. " + exception.getMessage()),
                UNAUTHORIZED);
    }

    private ErrorDto createErrorDto(String message) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setId(UUID.randomUUID());
        errorDto.setMessage(message);
        return errorDto;
    }
}
