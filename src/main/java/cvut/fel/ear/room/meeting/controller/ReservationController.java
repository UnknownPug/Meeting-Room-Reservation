package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.ReservationRequest;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Reservation>> getReservations() {
        return ResponseEntity.ok(service.getReservations());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable("id") long reservationId) {
        if (reservationId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        return ResponseEntity.ok(service.getReservationById(reservationId));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/between")
    public ResponseEntity<Iterable<Reservation>> getReservationsBetween(
            @RequestBody ReservationRequest reservationRequest) {
        return ResponseEntity.ok(service.getReservationsBetween(
                        reservationRequest.reservationDateTimeStart(), reservationRequest.reservationDateTimeEnd()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/{num}/limit")
    public ResponseEntity<List<Reservation>> getSortedReservationsByNum(
            @PathVariable int num,
            @RequestParam(value = "sort") String sortType) {
        if (num <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Number of reservations for sorting must be specified.");
        }
        if (sortType.equals("asc")) {
            return ResponseEntity.ok(service.getReservationsByNumAsc(num));
        } else if (sortType.equals("desc")) {
            return ResponseEntity.ok(service.getReservationsByNumDesc(num));
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to asc or desc to get the list of sorted" +
                            " reservations in ascending or descending order.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Iterable<Reservation>> getSortedReservations(
            @RequestBody ReservationRequest reservationRequest, @RequestParam(value = "sort") String sortType) {
        if (reservationRequest.reservationDateTimeStart() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Reservation start time for sorting must be specified.");
        }
        if (reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Reservation end time for sorting must be specified.");
        }
        if (sortType.equals("start")) {
            return ResponseEntity.ok(service.getReservationsStart(reservationRequest.reservationDateTimeStart()));
        } else if (sortType.equals("end")) {
            return ResponseEntity.ok(service.getReservationsEnd(reservationRequest.reservationDateTimeEnd()));
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Set the sort type to start or end to get a list" +
                    " of sorted reservations by start or end date time.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationRequest reservationRequest) {
        if (reservationRequest.reservationDateTimeStart() == null
                || reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Start time and end time for reservation must be specified.");
        }
        return ResponseEntity.ok(
                service.createReservation(
                        reservationRequest.reservationDateTimeStart(),
                        reservationRequest.reservationDateTimeEnd())
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping(path = "/room")
    public void addReservationRoom(@RequestBody ReservationRequest reservationRequest) {
        if (reservationRequest.id() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Reservation id must be specified.");
        }
        if (reservationRequest.roomId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room id must be specified.");
        }
        service.addReservationRoom(reservationRequest.id(), reservationRequest.roomId());
        LOG.debug(
                "Reservation with id {} was successfully added to room with id {}.",
                reservationRequest.id(), reservationRequest.roomId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/{id}")
    public void updateReservation(
            @PathVariable("id") long reservationId,
            @RequestBody ReservationRequest reservationRequest) {
        if (reservationId <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Reservation with this id does not found.");
        }
        if (reservationRequest.reservationDateTimeStart() == null
                || reservationRequest.reservationDateTimeEnd() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All Reservation fields must be completed.");
        }
        service.updateReservation(
                reservationId,
                reservationRequest.reservationDateTimeStart(),
                reservationRequest.reservationDateTimeEnd());
        LOG.debug("Reservation with id {} was successfully updated.", reservationId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deleteReservation(@PathVariable("id") long reservationId) {
        if (reservationId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        service.deleteReservation(reservationId);
        LOG.debug("Reservation with id {} was successfully deleted.", reservationId);
    }
}
