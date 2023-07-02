package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAdminsReturnsValidAdminsList() {
        when(adminRepository.findAll()).thenReturn(Collections.emptyList());
        Collection<Admin> admins = adminService.getAdmins();
        assertTrue(admins.isEmpty());
    }

    @Test
    public void testGetAdminByIdWhenAdminExists() {
        Long adminId = 1L;
        Admin admin = new Admin();
        admin.setId(adminId);
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
        Admin result = adminService.getAdminById(adminId);
        assertNotNull(result);
        assertEquals(admin, result);
        verify(adminRepository, times(1)).findById(adminId);
    }

    @Test
    public void testCreateAdminWithValidData() {
        // Given
        String username = "testuser";
        String email = "testuser@example.com";
        String password = "testpassword";

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);

        when(adminRepository.findAllByEmail(email)).thenReturn(false);
        when(adminRepository.findAllByUsername(username)).thenReturn(false);
        when(adminRepository.save(any())).thenReturn(admin);

        Admin result = adminRepository.save(admin);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(password, result.getPassword());
    }

    @Test
    public void testCreateAdminWithEmptyUsername() {
        String username = "";
        String email = "testuser@example.com";
        String password = "testpassword";
        when(adminRepository.findAllByEmail(email)).thenReturn(false);
        when(adminRepository.findAllByUsername(username)).thenReturn(false);
        assertThrows(ApplicationException.class, () -> adminService.createAdmin(username, email, password));
    }

    @Test
    public void testCreateAdminWithEmptyEmail() {
        String username = "testuser";
        String email = "";
        String password = "testpassword";
        when(adminRepository.findAllByEmail(email)).thenReturn(false);
        when(adminRepository.findAllByUsername(username)).thenReturn(false);
        assertThrows(ApplicationException.class, () -> adminService.createAdmin(username, email, password));
    }
}
