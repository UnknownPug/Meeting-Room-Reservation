package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.PaymentRepository;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import cvut.fel.ear.room.meeting.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    public final static Integer MAX_ROOM_CAPACITY = 5;

    private final UserRepository repository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PaymentRepository paymentRepository,
                       ReservationRepository reservationRepository,
                       PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Collection<User> getUsers() {
        return repository.findAll();
    }

    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + id + " does not exist."));
    }

    public User getUserByUsername(String username) {
        User user = repository.findByUsername(username);
        if (user == null) {
            throw new ApplicationException(
                    HttpStatus.NOT_FOUND, "User with username " + username + " does not exist.");
        }
        return user;
    }

    public User getUserByEmail(String email) {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with email " + email + " does not exist.");
        }
        return user;
    }

    public List<User> getUsersAsc() {
        return repository.findAllByIdIsNotNullOrderByUsernameAsc();
    }

    public List<User> getUsersDesc() {
        return repository.findAllByIdIsNotNullOrderByUsernameDesc();
    }

    public User createUser(String username, String email, String password) {
        User newUser = new User();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User info must be fully completed.");
        }
        if (repository.findByUsername(username) != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Username " + username + " is already taken.");
        }
        if (repository.findByEmail(email) != null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Email " + email + " is already taken.");
        }
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(Constants.DEFAULT_ROLE);
        newUser.encodePassword(passwordEncoder); // hashing password
        return repository.save(newUser);
    }

    public void addUserReservation(Long userId, Long reservationId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with Id " + userId + " does not exist."));
        Reservation reservation = getReservationById(reservationId, reservationRepository, MAX_ROOM_CAPACITY);
        if (!user.getUserHasReservation().contains(reservation)) {
            addToRoom(reservation);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "User already has a reservation in this room.");
        }
        reservationRepository.save(reservation);
        user.getUserHasReservation().add(reservation);
        repository.save(user);
    }

    public void addUserPayment(Long userId, Long paymentId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with Id " + userId + " does not exist."));
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + paymentId + " does not exist."));
        payment.setUserPayments(user);
        repository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, String username, String email, String password) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with Id " + userId + " does not exist."));
        if (username != null && username.length() > 0 && !Objects.equals(user.getUsername(), username)) {
            user.setUsername(username);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "User username must be filled completely or this username already set.");
        }
        if (email != null && email.length() > 0 && !Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "User email must be filled completely or this email already set.");
        }
        if (password != null && password.length() > 0 && !Objects.equals(user.getPassword(), password)) {
            user.setPassword(password);
            user.encodePassword(passwordEncoder); // hashing password
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "User password must be filled completely or this password already set.");
        }
        repository.save(user);
    }

    public void deleteUser(Long userId) {
        boolean exists = repository.existsById(userId);
        if (!exists) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "User with id " + userId + " does not exist.");
        }
        repository.deleteById(userId);
    }

    public void deleteReservationFromUser(Long userId, Long reservationId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "User with Id " + userId + " does not exist."));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + reservationId + " does not exist."));
        if (user.getUserHasReservation().contains(reservation)) {
            removeFromRoom(reservation);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "User " + user.getUsername() + " already does not have a reservation " +
                            " with id " + reservationId + ".");
        }
        user.getUserHasReservation().clear();
        reservationRepository.save(reservation);
        repository.save(user);
    }

    public static void addToRoom(Reservation reservation) {
        int newRoomCapacity = reservation.getRoomReservation().getRoomCapacity() + 1;
        reservation.getRoomReservation().setRoomCapacity(newRoomCapacity);
    }

    public static void removeFromRoom(Reservation reservation) {
        int newRoomCapacity = reservation.getRoomReservation().getRoomCapacity() - 1;
        if (newRoomCapacity <= -1) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room is already empty.");
        }
        reservation.getRoomReservation().setRoomCapacity(newRoomCapacity);
    }

    public static Reservation getReservationById(Long reservationId,
                                                 ReservationRepository resRepository,
                                                 Integer maxRoomCapacity) {
        Reservation reservation = resRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + reservationId + " does not exist."));
        if (reservation.getRoomReservation() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room reservation is null.");
        }
        if (reservation.getRoomReservation().getRoomCapacity() >= maxRoomCapacity) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room capacity is full.");
        }
        return reservation;
    }

    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return repository.findByUsername(username) != null;
    }

    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        return repository.findByEmail(email) != null;
    }
}
