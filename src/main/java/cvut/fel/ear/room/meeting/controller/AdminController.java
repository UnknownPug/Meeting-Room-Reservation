package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.AdminRequest;
import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Admin>> getAdmins() {
        return ResponseEntity.ok(adminService.getAdmins());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable long id) {
        if (id <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<List<Admin>> getSortedAdmins(@RequestParam(value = "sort") String sortType) {
        if (sortType.equals("asc")) {
            return ResponseEntity.ok(adminService.getAdminsAsc());
        } else if (sortType.equals("desc")) {
            return ResponseEntity.ok(adminService.getAdminsDesc());
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Set the sort type to asc or desc to get the list of sorted" +
                            " admins in ascending or descending order.");
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.username() == null
                || adminRequest.email() == null
                || adminRequest.password() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "All Admin fields must be completed.");
        }
        return ResponseEntity.ok(
                adminService.createAdmin(adminRequest.username(), adminRequest.email(), adminRequest.password())
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/room")
    public void adminControlRoom(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.id() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        if (adminRequest.roomId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Room id must be specified.");
        }
        adminService.adminControlRoom(adminRequest.id(), adminRequest.roomId());
        LOG.debug("Admin {} successfully controlling room with id {}.", adminRequest.username(), adminRequest.roomId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/reservation")
    public void addAdminReservation(@RequestBody AdminRequest adminRequest) {
        if (adminRequest.id() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        if (adminRequest.reservationId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        adminService.addAdminReservation(adminRequest.id(), adminRequest.reservationId());
        LOG.debug("Admin {} successfully set reservation with id {}.",
                adminRequest.username(), adminRequest.reservationId());
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/{id}")
    public void updateAdmin(@PathVariable("id") long adminId, @RequestBody AdminRequest adminRequest) {
        if (adminId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        if (adminRequest.email() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "The email field for the Admin must be completed.");
        }
        adminService.updateAdmin(adminId, adminRequest.email());
        LOG.debug("Admin {} was successfully updated.", adminRequest.username());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{id}")
    public void deleteAdmin(@PathVariable("id") long adminId) {
        if (adminId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        adminService.deleteAdmin(adminId);
        LOG.debug("Admin {} was successfully deleted.", adminService.getAdminById(adminId).getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{adId}/reservation/{resId}")
    public void deleteAdminReservation(@PathVariable("adId") long adId, @PathVariable("resId") long resId) {
        if (adId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin id must be specified.");
        }
        if (resId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        adminService.deleteAdminReservation(adId, resId);
        LOG.debug("Reservation with id {} for Admin {} was successfully deleted.", resId,
                adminService.getAdminById(adId).getUsername());
    }
}
