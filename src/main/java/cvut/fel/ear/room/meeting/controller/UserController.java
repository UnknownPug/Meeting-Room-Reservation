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
import java.util.Optional;

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
    @GetMapping(path = "/id/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable("id") Long userId) {
        if (userId == null) {
            throw new ApplicationException("User does not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/username/{username}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getUserByUsername(@PathVariable("username") String username) {
        if (username == null) {
            throw new ApplicationException("User does not found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{email}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        if (email == null) {
            throw new ApplicationException("User does not found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/filter")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsersFilter(@RequestParam(value = "sort") String filterType) {
        if (filterType.equals("asc")) {
            return ResponseEntity.ok(userService.getUsersAsc());
        } else if (filterType.equals("desc")) {
            return ResponseEntity.ok(userService.getUsersDesc());
        } else {
            throw new ApplicationException("Filter type must be specified.", HttpStatus.BAD_REQUEST);
        }

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody UserRequest userRequest) {
        if (userRequest.username() == null || userRequest.email() == null || userRequest.password() == null) {
            throw new ApplicationException("User parameters must be completed.", HttpStatus.BAD_REQUEST);
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
    @PostMapping(path = "/payment")
    public void addUserPayment(@RequestBody UserRequest userRequest) {
        if (userRequest.id() == null) {
            throw new ApplicationException("User does not found.", HttpStatus.NOT_FOUND);
        }
        if (userRequest.paymentId() == null) {
            throw new ApplicationException("Payment does not found.", HttpStatus.NOT_FOUND);
        }
        userService.addUserPayment(userRequest.id(), userRequest.paymentId());
        LOG.debug("Payment {} successfully added to user {}.", userRequest.paymentId(), userRequest.username());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/reservation")
    public void addUserReservation(@RequestBody UserRequest userRequest) {
        if (userRequest.id() == null) {
            throw new ApplicationException("User does not found.", HttpStatus.NOT_FOUND);
        }
        if (userRequest.reservationId() == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        userService.addUserReservation(userRequest.id(), userRequest.reservationId());
        LOG.debug("User {} reservation created.", userRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public void updateUser(
            @PathVariable("id") Long userId,
            @RequestBody UserRequest userRequest) {
        if (userId == null
                || userRequest.username() == null
                || userRequest.email() == null
                || userRequest.password() == null) {
            throw new ApplicationException("User parameters must be completed.", HttpStatus.BAD_REQUEST);

        }
        userService.updateUser(userId, userRequest.username(), userRequest.email(), userRequest.password());
        LOG.debug("User {} successfully updated.", userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteUser(@PathVariable("id") Long userId) {
        if (userId == null) {
            throw new ApplicationException("User does not found.", HttpStatus.NOT_FOUND);
        }
        userService.deleteUser(userId);
        LOG.debug("User {} successfully deleted.", userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{usId}/reservation/{resId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public void deleteReservationFromUser(@PathVariable("usId") Long usId, @PathVariable("resId") Long resId) {
        if (usId == null) {
            throw new ApplicationException("User does not found.", HttpStatus.NOT_FOUND);
        }
        if (resId == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        userService.deleteReservationFromUser(usId, resId);
        LOG.debug("Reservation {} was successfully deleted from user {}.", resId, usId);
    }
}