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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void getAdminsReturnsValidAdminList() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdmins()).thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testGetAdminByIdReturnsValidAdmin() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdminById(anyLong())).thenReturn(admin);

        mockMvc.perform(get("/admin/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getSortedAdminsReturnsAdminListSortedAscending() throws Exception {
        Admin admin = new Admin();
        admin.setId(1L);
        when(adminService.getAdminsAsc()).thenReturn(Collections.singletonList(admin));

        mockMvc.perform(get("/admin?sort=asc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void testDeleteAdminReturnsSuccessfulDeletedAdmin() throws Exception {
        Long adminId = 1L;
        doNothing().when(adminService).deleteAdmin(adminId);
        when(adminService.getAdminById(adminId)).thenReturn(new Admin());
        AdminController adminController = new AdminController(adminService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/{id}", adminId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void testDeleteAdminReservationReturnsSuccessfulDeletedAdminReservation() throws Exception {
        Long adminId = 1L;
        long reservationId = 2L;
        doNothing().when(adminService).deleteAdminReservation(adminId, reservationId);
        when(adminService.getAdminById(adminId)).thenReturn(new Admin());
        AdminController adminController = new AdminController(adminService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/{adId}/reservation/{resId}", adminId, reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}