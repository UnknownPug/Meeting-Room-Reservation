package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.ReservationRequest;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.service.ReservationService;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping(path = "/reservation")
public class ReservationController {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService service;

    @Autowired
    public ReservationController(ReservationService service) {
        this.service = service;
    }

    /*
     * ROLE_ADMIN usage Reservation
     */

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/list")
    public ResponseEntity<Iterable<Reservation>> getReservations() {
        return ResponseEntity.ok(service.getReservations());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Optional<Reservation>> getReservationById(@PathVariable("id") Long reservationId) {
        if (reservationId == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(service.getReservationById(reservationId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/list/between")
    public ResponseEntity<Iterable<Reservation>> getReservationsBetween(
            @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(service.getReservationsBetween(
                        reservationRequest.reservationDateTimeStart(), reservationRequest.reservationDateTimeEnd()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{num}/filter")
    public ResponseEntity<ArrayList<Reservation>> getReservationsByNumFilter(
            @PathVariable Integer num,
            @RequestParam(value = "sort") String filterType) {
        if (num == null) {
            throw new ApplicationException("Number of reservations not specified.", HttpStatus.BAD_REQUEST);
        }
        if (filterType.equals("asc")) {
            return ResponseEntity.ok(service.getReservationsByNumAsc(num));
        } else if (filterType.equals("desc")) {
            return ResponseEntity.ok(service.getReservationsByNumDesc(num));
        } else {
            throw new ApplicationException("Filter type must be specified.", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/filter")
    public ResponseEntity<Iterable<Reservation>> getReservationsFilter(
            @RequestBody ReservationRequest reservationRequest, @RequestParam(value = "sort") String filterType) {
        if (reservationRequest.reservationDateTimeStart() == null) {
            throw new ApplicationException("Start time does not specified.", HttpStatus.BAD_REQUEST);
        }
        if (reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException("End time does not specified.", HttpStatus.BAD_REQUEST);
        }
        if (filterType.equals("start")) {
            return ResponseEntity.ok(service.getReservationsStart(reservationRequest.reservationDateTimeStart()));
        } else if (filterType.equals("end")) {
            return ResponseEntity.ok(service.getReservationsEnd(reservationRequest.reservationDateTimeEnd()));
        } else {
            throw new ApplicationException("Filter type must be specified.", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest reservationRequest) {
        if (reservationRequest.reservationDateTimeStart() == null
                || reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException(
                    "Start time and end time for reservation must be specified.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                service.createReservation(
                        reservationRequest.reservationDateTimeStart(),
                        reservationRequest.reservationDateTimeEnd())
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/room")
    public void addReservationRoom(@RequestBody ReservationRequest reservationRequest) {
        if (reservationRequest.id() == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        if (reservationRequest.roomId() == null) {
            throw new ApplicationException("Room does not found.", HttpStatus.NOT_FOUND);
        }
        service.addReservationRoom(reservationRequest.id(), reservationRequest.roomId());
        LOG.debug(
                "Reservation {} successfully added to room {}.", reservationRequest.id(), reservationRequest.roomId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/{id}")
    public void updateReservation(
            @PathVariable("id") Long reservationId,
            @RequestBody ReservationRequest reservationRequest) {
        if (reservationId == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        if (reservationRequest.reservationDateTimeStart() == null
                || reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException("Reservation parameters must be completed.", HttpStatus.BAD_REQUEST);
        }
        service.updateReservation(
                reservationId,
                reservationRequest.reservationDateTimeStart(),
                reservationRequest.reservationDateTimeEnd());
        LOG.debug("Reservation {} successfully updated.", reservationId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deleteReservation(@PathVariable("id") Long reservationId) {
        if (reservationId == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        service.deleteReservation(reservationId);
        LOG.debug("Reservation {} successfully deleted.", reservationId);
    }
}
