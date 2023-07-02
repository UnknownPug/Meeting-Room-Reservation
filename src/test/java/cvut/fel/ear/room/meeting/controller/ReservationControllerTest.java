package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.ReservationRequest;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ReservationControllerTest {

    @Mock
    private ReservationService service;

    private ReservationController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new ReservationController(service);
    }

    @Test
    public void testGetReservationsReturnsValidReservationsList() {
        when(service.getReservations()).thenReturn(new ArrayList<>());
        ResponseEntity<Iterable<Reservation>> response = controller.getReservations();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReservationByIdReturnsValidReservation() {
        long reservationId = 1L;
        when(service.getReservationById(reservationId)).thenReturn(new Reservation());
        ResponseEntity<Reservation> response = controller.getReservationById(reservationId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReservationByIdReturnsNotFoundStatus() {
        try {
            controller.getReservationById(0);
        } catch (ApplicationException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        }
    }

    @Test
    public void getReservationsBetweenReturnsReservationList() {
        ReservationRequest reservationRequest = new ReservationRequest(1L,
                1L, 1L,
                10D,
                LocalDateTime.now(),
                LocalDateTime.now());
        when(service.getReservationsBetween(null, null)).thenReturn(new ArrayList<>());
        ResponseEntity<Iterable<Reservation>> response = controller.getReservationsBetween(reservationRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getSortedReservationsByNumReturnsReservationList() {
        int num = 1;
        String sortType = "asc";
        when(service.getReservationsByNumAsc(num)).thenReturn(new ArrayList<>());
        ResponseEntity<List<Reservation>> response = controller.getSortedReservationsByNum(num, sortType);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getReservationsByNumFilterReturnsBadRequestStatus() {
        String filterType = "invalid";
        try {
            controller.getSortedReservationsByNum(0, filterType);
        } catch (ApplicationException ex) {
            assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        }
    }

}
