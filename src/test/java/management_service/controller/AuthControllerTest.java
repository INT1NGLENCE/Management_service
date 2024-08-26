package management_service.controller;

import management.system.controller.AuthController;
import management.system.dto.LoginRequest;
import management.system.dto.RegisterRequest;
import management.system.dto.UserDto;
import management.system.mapper.ManagementMapper;
import management.system.model.AppUser;
import management.system.repository.UserRepository;
import management.system.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ManagementMapper managementMapper;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testAuthenticateUser_Success() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        ResponseEntity<Object> response = authController.authenticateUser(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
    }

    @Test
    public void testAuthenticateUser_InvalidData() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authController.authenticateUser(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid email or password", exception.getReason());
    }

    @Test
    public void testRegisterUser_Success() {
        RegisterRequest registerRequest = new RegisterRequest("newuser@example.com", "password");
        AppUser appUser = new AppUser();
        appUser.setEmail(registerRequest.getEmail());
        appUser.setPassword("encodedpassword");
        UserDto userDto = new UserDto();
        userDto.setEmail(registerRequest.getEmail());

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedpassword");
        when(userRepository.save(any(AppUser.class))).thenReturn(appUser);
        when(managementMapper.toUserDto(appUser)).thenReturn(userDto);

        ResponseEntity<UserDto> response = authController.registerUser(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userDto, response.getBody());

        verify(userRepository, times(1)).save(any(AppUser.class));
    }

    @Test
    public void testRegisterUser_EmailInUse() {
        RegisterRequest registerRequest = new RegisterRequest("existinguser@example.com", "password");

        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                authController.registerUser(registerRequest));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Email is already in use", exception.getReason());
    }
}