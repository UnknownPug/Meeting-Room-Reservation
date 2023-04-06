package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.RoomRequest;
import cvut.fel.ear.room.meeting.entity.Room;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping(path = "/room")
public class RoomController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
    private final RoomService service;

    @Autowired
    public RoomController(RoomService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list")
    public ResponseEntity<Iterable<Room>> getRooms(@RequestParam(value = "free") Boolean isFree) {
        if (isFree) {
            return ResponseEntity.ok(service.getFreeRooms());
        } else {
            return ResponseEntity.ok(service.getRooms());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/filter")
    public ResponseEntity<Room> getRoomByName(@RequestParam(value = "name") String roomName) {
        if (roomName == null) {
            throw new ApplicationException("Room name not set.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(service.getRoomByName(roomName));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list/free/between/time")
    public ResponseEntity<Iterable<Room>> getFreeRoomsBetweenTime(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                service.getFreeRoomsBetweenTime(
                        request.reservationDateTimeStart(),
                        request.reservationDateTimeEnd()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/capacity/{num}/filter")
    public ResponseEntity<ArrayList<Room>> getRoomsByCapacityFilter(
            @PathVariable Integer num,
            @RequestParam(value = "sort") String filterType) {
        if (num == null) {
            throw new ApplicationException("Number must be specified.", HttpStatus.BAD_REQUEST);
        }
        if (filterType.equals("asc")) {
            return ResponseEntity.ok(service.getRoomsByCapacityAsc(num));
        } else {
            throw new ApplicationException("Filter type must be specified (can be only asc).", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{num}/filter")
    public ResponseEntity<ArrayList<Room>> getRoomsByNumFilter(@PathVariable Integer num,
                                                               @RequestParam(value = "sort") String filterType) {
        if (num == null) {
            throw new ApplicationException("Number must be specified.", HttpStatus.BAD_REQUEST);
        }
        if (filterType.equals("asc")) {
            return ResponseEntity.ok(service.getRoomsByNumAsc(num));
        } else if (filterType.equals("desc")) {
            return ResponseEntity.ok(service.getRoomsByNumDesc(num));
        } else {
            throw new ApplicationException("Filter type must be specified.", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest request) {
        if (request.name() == null ||
                request.pricePerHour() == null ||
                request.description() == null) {
            throw new ApplicationException("Room parameters must be completed.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                service.createRoom(
                        request.name(),
                        request.pricePerHour(),
                        request.description()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(path = "/{id}")
    public void updateRoom(@PathVariable("id") Long roomId, @RequestBody RoomRequest request) {
        if (roomId == null) {
            throw new ApplicationException("Room does not found.", HttpStatus.NOT_FOUND);
        }
        if (request.name() == null || request.pricePerHour() == null || request.description() == null) {
            throw new ApplicationException("Room parameters must be completed.", HttpStatus.BAD_REQUEST);
        }
        service.updateRoom(roomId, request.name(), request.pricePerHour(), request.description());
        LOG.debug("Room {} successfully updated.", request.name());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deleteRoom(@PathVariable("id") Long roomId) {
        if (roomId == null) {
            throw new ApplicationException("Room not found", HttpStatus.NOT_FOUND);
        }
        service.deleteRoom(roomId);
        LOG.debug("Room {} successfully deleted.", roomId);
    }
}