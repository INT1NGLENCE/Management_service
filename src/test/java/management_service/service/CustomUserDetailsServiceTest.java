package management_service.service;

import management.system.model.AppUser;
import management.system.repository.UserRepository;
import management.system.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    public void testLoadUserByUsername_Success() {
        String email = "test@example.com";
        String password = "password123";

        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(appUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(email);
        });

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testLoadUserByUsername_MethodCalled() {
        String email = "test@example.com";
        String password = "password123";

        AppUser appUser = new AppUser();
        appUser.setEmail(email);
        appUser.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(appUser);

        customUserDetailsService.loadUserByUsername(email);

        verify(userRepository, times(1)).findByEmail(email);
    }
}
