package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.AdminRequest;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.service.AdminService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list")
    public ResponseEntity<Iterable<Admin>> getAdmins() {
        return ResponseEntity.ok(adminService.getAdmins());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public ResponseEntity<Optional<Admin>> getAdminById(@PathVariable Long id) {
        if (id == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/filter")
    public ResponseEntity<List<Admin>> getAdminsFilter(@RequestParam(value = "sort") String filterType) {
        if (filterType.equals("asc")) {
            return ResponseEntity.ok(adminService.getAdminsAsc());
        } else if (filterType.equals("desc")) {
            return ResponseEntity.ok(adminService.getAdminsDesc());
        } else {
            throw new ApplicationException("Filter type must be specified.", HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.username() == null
                || adminRequest.email() == null
                || adminRequest.password() == null) {
            throw new ApplicationException("Admin must have all fields completed.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                adminService.createAdmin(adminRequest.username(), adminRequest.email(), adminRequest.password())
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/room")
    public void adminControlRoom(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.id() == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        if (adminRequest.roomId() == null) {
            throw new ApplicationException("Room does not found.", HttpStatus.NOT_FOUND);
        }
        adminService.adminControlRoom(adminRequest.id(), adminRequest.roomId());
        LOG.debug("Admin {} successfully controlling room {}.", adminRequest.username(), adminRequest.roomId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/reservation")
    public void addAdminReservation(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.id() == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        if (adminRequest.reservationId() == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        adminService.addAdminReservation(adminRequest.id(), adminRequest.reservationId());
        LOG.debug("Admin {} successfully set reservation {}.", adminRequest.username(), adminRequest.reservationId());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public void updateAdmin(@PathVariable("id") Long adminId, @RequestBody AdminRequest adminRequest) {
        if (adminId == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        if (adminRequest.email() == null) {
            throw new ApplicationException("Email filed must be completed.", HttpStatus.BAD_REQUEST);
        }
        adminService.updateAdmin(adminId, adminRequest.email());
        LOG.debug("Admin {} successfully updated.", adminRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    public void deleteAdmin(@PathVariable("id") Long adminId) {
        if (adminId == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        adminService.deleteAdmin(adminId);
        LOG.debug("Admin {} successfully deleted.", adminId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{adId}/reservation/{resId}")
    public void deleteAdminReservation(@PathVariable("adId") Long adId, @PathVariable("resId") Long resId) {
        if (adId == null) {
            throw new ApplicationException("Admin does not found.", HttpStatus.NOT_FOUND);
        }
        if (resId == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        adminService.deleteAdminReservation(adId, resId);
        LOG.debug("Reservation {} for admin {} successfully deleted.", resId, adId);
    }
}
