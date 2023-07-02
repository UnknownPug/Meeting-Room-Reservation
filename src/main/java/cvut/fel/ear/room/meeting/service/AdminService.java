package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.entity.Room;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.AdminRepository;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import cvut.fel.ear.room.meeting.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class AdminService {

    public final static Integer MAX_ROOM_CAPACITY = 5;

    private final AdminRepository repository;
    private final RoomRepository roomRepository;
    private final ReservationRepository resRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(AdminRepository repository,
                        RoomRepository roomRepository,
                        ReservationRepository resRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roomRepository = roomRepository;
        this.resRepository = resRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Collection<Admin> getAdmins() {
        return repository.findAll();
    }

    public Admin getAdminById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Admin with id " + id + " does not exist."));
    }

    public Admin createAdmin(String username, String email, String password) {
        Admin admin = new Admin();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Admin info must be completely filled.");
        }
        if (repository.findByEmail(email) != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email " + email + " is already taken.");
        }
        if (repository.findByUsername(username) != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username " + username + " is already taken.");
        }
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.encodePassword(passwordEncoder);
        return repository.save(admin);
    }

    public void addAdminReservation(Long id, Long reservationId) {
        Admin admin = repository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Admin with id " + id + " does not exist."));
        Reservation reservation = UserService.getReservationById(reservationId, resRepository, MAX_ROOM_CAPACITY);
        if (!admin.getAdminReservations().contains(reservation)) {
            UserService.addToRoom(reservation);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User already has a reservation in this room.");
        }
        resRepository.save(reservation);
        admin.getAdminReservations().add(reservation);
        repository.save(admin);
    }

    public void adminControlRoom(Long adminId, Long roomId) {
        Admin admin = repository.findById(adminId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Admin with Id " + adminId + " does not exist."));
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + roomId + " does not exist."));
        admin.getAdminControlRoom().add(room);
        repository.save(admin);
    }

    public List<Admin> getAdminsAsc() {
        return repository.findAllByIdIsNotNullOrderByUsernameAsc();
    }

    public List<Admin> getAdminsDesc() {
        return repository.findAllByIdIsNotNullOrderByUsernameDesc();
    }

    @Transactional
    public void updateAdmin(Long adminId, String email) {
        Admin admin = repository.findById(adminId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Admin with Id " + adminId + " does not exist."));
        if (email != null && email.length() > 0 && !Objects.equals(admin.getEmail(), email)) {
            admin.setEmail(email);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Admin email must be set.");
        }
        repository.save(admin);
    }

    public void deleteAdmin(Long adminId) {
        boolean exists = repository.existsById(adminId);
        if (!exists) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Admin with id " + adminId + " does not exist.");
        }
        repository.deleteById(adminId);
    }

    public void deleteAdminReservation(long adminId, long reservationId) {
        Admin admin = repository.findById(adminId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "User with Id " + adminId + " does not exist."));
        Reservation reservation = resRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + reservationId + " does not exist."));
        if (admin.getAdminReservations().contains(reservation)) {
            UserService.removeFromRoom(reservation);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Admin " + admin.getUsername() +
                    " does not have a reservation with id " + reservationId + ".");
        }
        admin.getAdminReservations().clear();
        resRepository.save(reservation);
        repository.save(admin);
    }
}
