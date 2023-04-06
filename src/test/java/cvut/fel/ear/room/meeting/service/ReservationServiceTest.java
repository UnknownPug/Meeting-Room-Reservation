package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import cvut.fel.ear.room.meeting.repository.RoomRepository;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationService(reservationRepository, roomRepository, userRepository);
    }

    @Test
    void getReservations() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findAll()).thenReturn(new ArrayList<>() {{
            add(reservation);
        }});

        Collection<Reservation> reservations = reservationService.getReservations();

        assertEquals(1, reservations.size());
        assertEquals(1L, reservations.iterator().next().getId());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void getReservationById() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Optional<Reservation> optionalReservation = reservationService.getReservationById(1L);

        assertTrue(optionalReservation.isPresent());
        assertEquals(1L, optionalReservation.get().getId());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationByIdNonExistent() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ApplicationException.class, () -> reservationService.getReservationById(1L));
        verify(reservationRepository, times(1)).findById(1L);
    }
}