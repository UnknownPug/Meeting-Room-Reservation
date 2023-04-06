package cvut.fel.ear.room.meeting.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import cvut.fel.ear.room.meeting.dto.request.ReservationRequest;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.ReservationService;

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
    public void testGetReservations() {
        when(service.getReservations()).thenReturn(new ArrayList<>());
        ResponseEntity<Iterable<Reservation>> response = controller.getReservations();
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReservationById() {
        Long reservationId = 1L;
        when(service.getReservationById(reservationId)).thenReturn(Optional.of(new Reservation()));
        ResponseEntity<Optional<Reservation>> response = controller.getReservationById(reservationId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReservationByIdNotFound() {
        try {
            controller.getReservationById(null);
        } catch (ApplicationException ex) {
            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }
    }

    @Test
    public void testGetReservationsBetween() {
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
    public void testGetReservationsByNumFilter() {
        Integer num = 1;
        String filterType = "asc";
        when(service.getReservationsByNumAsc(num)).thenReturn(new ArrayList<>());
        ResponseEntity<ArrayList<Reservation>> response = controller.getReservationsByNumFilter(num, filterType);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetReservationsByNumFilterBadRequest() {
        String filterType = "invalid";
        try {
            controller.getReservationsByNumFilter(null, filterType);
        } catch (ApplicationException ex) {
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }
    }

}
