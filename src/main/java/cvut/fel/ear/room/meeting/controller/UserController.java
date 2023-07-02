package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.UserRequest;
import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/data")
    public ResponseEntity<User> getUser(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String username,
                                        @RequestParam(required = false) String email) {
        if (id != null && id > 0) {
            if (username != null || email != null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Specify only one of id, username, or email.");
            }
            return ResponseEntity.ok(userService.getUserById(id));
        } else if (username != null) {
            if (email != null) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Specify only one of id, username, or email.");
            }
            return ResponseEntity.ok(userService.getUserByUsername(username));
        } else if (email != null) {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Specify one of id, username, or email.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getSortedUsers(@RequestParam(value = "sort") String sortType) {
        if (sortType.equals("asc")) {
            return ResponseEntity.ok(userService.getUsersAsc());
        } else if (sortType.equals("desc")) {
            return ResponseEntity.ok(userService.getUsersDesc());
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to asc or desc to get the list of sorted" +
                            " users in ascending or descending order.");
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        if (userRequest.username() == null || userRequest.email() == null || userRequest.password() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All User fields must be completed.");
        }
        return ResponseEntity.ok(
                userService.createUser(
                        userRequest.username(),
                        userRequest.email(),
                        userRequest.password()
                )
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(path = "/payment")
    public void addUserPayment(@RequestBody UserRequest userRequest) {
        if (userRequest.id() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (userRequest.paymentId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Payment id must be specified.");
        }
        userService.addUserPayment(userRequest.id(), userRequest.paymentId());
        LOG.debug("Payment with id {} was successfully added to a User with id {}.",
                userRequest.paymentId(), userRequest.username());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping(path = "/reservation")
    public void addUserReservation(@RequestBody UserRequest userRequest) {
        if (userRequest.id() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (userRequest.reservationId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        userService.addUserReservation(userRequest.id(), userRequest.reservationId());
        LOG.debug("User {} has successfully made a reservation.", userRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping(path = "/{id}")
    public void updateUser(
            @PathVariable("id") long userId,
            @RequestBody UserRequest userRequest) {
        if (userId <= 0
                || userRequest.username() == null
                || userRequest.email() == null
                || userRequest.password() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All User fields must be completed.");

        }
        userService.updateUser(userId, userRequest.username(), userRequest.email(), userRequest.password());
        LOG.debug("User {} was successfully updated.", userRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") long userId) {
        if (userId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        userService.deleteUser(userId);
        LOG.debug("User {} successfully deleted.", userService.getUserById(userId).getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping(path = "/{usId}/reservation/{resId}")
    public void deleteReservationFromUser(@PathVariable("usId") long usId, @PathVariable("resId") long resId) {
        if (usId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User id must be specified.");
        }
        if (resId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        userService.deleteReservationFromUser(usId, resId);
        LOG.debug("Reservation with id {} was successfully deleted from user {}.",
                resId, userService.getUserById(usId).getUsername());
    }
}