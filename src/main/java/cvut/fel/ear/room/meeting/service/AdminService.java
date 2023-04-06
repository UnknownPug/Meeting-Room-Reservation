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
import java.util.Optional;

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

    public Optional<Admin> getAdminById(Long id) {
        Admin admin = repository.findById(id).orElseThrow(
                () -> new ApplicationException("Admin with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
        return repository.findById(admin.getId());
    }

    public Admin createAdmin(String username, String email, String password) {
        Admin admin = new Admin();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ApplicationException("Admin info must be completely filled.", HttpStatus.BAD_REQUEST);
        }
        if (repository.findByEmail(email) != null) {
            throw new ApplicationException("Email " + email + " is already taken.", HttpStatus.BAD_REQUEST);
        }
        if (repository.findByUsername(username) != null) {
            throw new ApplicationException("Username " + username + " is already taken.", HttpStatus.BAD_REQUEST);
        }
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(password);
        admin.encodePassword(passwordEncoder);
        return repository.save(admin);
    }

    public void addAdminReservation(Long id, Long reservationId) {
        Admin admin = repository.findById(id).orElseThrow(
                () -> new ApplicationException("Admin with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
        Reservation reservation = reservationById(reservationId, resRepository, MAX_ROOM_CAPACITY);
        if (!admin.getAdminReservations().contains(reservation)) {
            addToRoom(reservation);
        } else {
            throw new ApplicationException("User already has a reservation in this room.", HttpStatus.BAD_REQUEST);
        }
        resRepository.save(reservation);
        admin.getAdminReservations().add(reservation);
        repository.save(admin);
    }

    static Reservation reservationById(Long reservationId, ReservationRepository resRepository,
                                       Integer maxRoomCapacity) {
        Reservation reservation = resRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        if (reservation.getRoomReservation() == null) {
            throw new ApplicationException("Room reservation is null.", HttpStatus.BAD_REQUEST);
        }
        if (reservation.getRoomReservation().getRoomCapacity() >= maxRoomCapacity) {
            throw new ApplicationException("Room capacity is full.", HttpStatus.BAD_REQUEST);
        }
        return reservation;
    }

    public void adminControlRoom(Long adminId, Long roomId) {
        Admin admin = repository.findById(adminId).orElseThrow(
                () -> new ApplicationException(
                        "Admin with Id " + adminId + " does not exist.", HttpStatus.NOT_FOUND));
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + roomId + " does not exist.", HttpStatus.NOT_FOUND));
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
                        "Admin with Id " + adminId + " does not exist.", HttpStatus.NOT_FOUND));
        if (email != null && email.length() > 0 && !Objects.equals(admin.getEmail(), email)) {
            admin.setEmail(email);
        } else {
            throw new ApplicationException("Admin email must be set.", HttpStatus.BAD_REQUEST);
        }
        repository.save(admin);
    }

    public void deleteAdmin(Long adminId) {
        boolean exists = repository.existsById(adminId);
        if (!exists) {
            throw new ApplicationException("Admin with id " + adminId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(adminId);
    }

    public void deleteAdminReservation(Long adminId, Long reservationId) {
        Admin admin = repository.findById(adminId).orElseThrow(
                () -> new ApplicationException("User with Id " + adminId + " does not exist.", HttpStatus.NOT_FOUND));
        Reservation reservation = resRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        if (admin.getAdminReservations().contains(reservation)) {
            removeFromRoom(reservation);
        } else {
            throw new ApplicationException(
                    "Admin " + admin.getUsername() +
                            " does not have a reservation with id " + reservationId + ".", HttpStatus.BAD_REQUEST);
        }
        admin.getAdminReservations().clear();
        resRepository.save(reservation);
        repository.save(admin);
    }

    private static void addToRoom(Reservation reservation) {
        int newRoomCapacity = reservation.getRoomReservation().getRoomCapacity() + 1;
        reservation.getRoomReservation().setRoomCapacity(newRoomCapacity);
    }

    private static void removeFromRoom(Reservation reservation) {
        int newRoomCapacity = reservation.getRoomReservation().getRoomCapacity() - 1;
        if (newRoomCapacity <= -1) {
            throw new ApplicationException("Room is already empty.", HttpStatus.BAD_REQUEST);
        }
        reservation.getRoomReservation().setRoomCapacity(newRoomCapacity);
    }
}
