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

import java.util.List;


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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Room>> getRooms(@RequestParam(value = "free") Boolean isFree) {
        if (isFree) {
            return ResponseEntity.ok(service.getFreeRooms());
        } else {
            return ResponseEntity.ok(service.getRooms());
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping
    public ResponseEntity<Room> getRoomByName(@RequestParam(value = "name") String roomName) {
        if (roomName == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room name must be specified.");
        }
        return ResponseEntity.ok(service.getRoomByName(roomName));
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/free")
    public ResponseEntity<Iterable<Room>> getFreeRoomsBetweenTime(@RequestBody RoomRequest request) {
        return ResponseEntity.ok(
                service.getFreeRoomsBetweenTime(
                        request.reservationDateTimeStart(),
                        request.reservationDateTimeEnd()
                )
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/{num}/capacity")
    public ResponseEntity<List<Room>> getSortedRoomsByCapacity(
            @PathVariable int num,
            @RequestParam(value = "sort") String sortType) {
        if (num <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "You must specify the number of capacity for the correct sorting of rooms.");
        }
        if (sortType.equals("asc")) {
            return ResponseEntity.ok(service.getRoomsByCapacityAsc(num));
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to asc to get the list of sorted rooms in ascending order.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(path = "/{num}/limit")
    public ResponseEntity<List<Room>> getSortedRoomsByNum(@PathVariable int num,
                                                          @RequestParam(value = "sort") String sortType) {
        if (num <= 0) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "You must specify the number of limit for the correct sorting of rooms.");
        }
        if (sortType.equals("asc")) {
            return ResponseEntity.ok(service.getRoomsByNumAsc(num));
        } else if (sortType.equals("desc")) {
            return ResponseEntity.ok(service.getRoomsByNumDesc(num));
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to asc or desc to get the list of sorted" +
                            " rooms in ascending or descending order.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomRequest request) {
        if (request.name() == null ||
                request.pricePerHour() == null ||
                request.description() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All Room fields must be completed.");
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
    public void updateRoom(@PathVariable("id") long roomId, @RequestBody RoomRequest request) {
        if (roomId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room id must be specified.");
        }
        if (request.name() == null || request.pricePerHour() == null || request.description() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All Room requested fields must be completed.");
        }
        service.updateRoom(roomId, request.name(), request.pricePerHour(), request.description());
        LOG.debug("Room {} was successfully updated.", request.name());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deleteRoom(@PathVariable("id") long roomId) {
        if (roomId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room id must be specified.");
        }
        service.deleteRoom(roomId);
        LOG.debug("Room {} was successfully deleted.", service.getRoomById(roomId).getName());
    }
}