package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Test
    void testGetAdmins() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdmins()).thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get("/admin/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testGetAdminById() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdminById(anyLong())).thenReturn(Optional.of(admin));

        mockMvc.perform(get("/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void testGetAdminsFilter() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdminsAsc()).thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get("/admin/filter?sort=asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testDeleteAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteAdminReservation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/1/reservation/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}