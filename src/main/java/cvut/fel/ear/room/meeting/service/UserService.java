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
import java.util.Optional;

@Service
public class UserService {

    public final static Integer MAX_ROOM_CAPACITY = 5;

    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       PaymentRepository paymentRepository,
                       ReservationRepository reservationRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public Collection<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApplicationException("User with id " + id + " does not exist.", HttpStatus.NOT_FOUND));
        return userRepository.findById(user.getId());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getUsersAsc() {
        return userRepository.findAllByIdIsNotNullOrderByUsernameAsc();
    }

    public List<User> getUsersDesc() {
        return userRepository.findAllByIdIsNotNullOrderByUsernameDesc();
    }

    public User createUser(String username, String email, String password) {
        User newUser = new User();
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            throw new ApplicationException("User info must be fully completed.", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsername(username) != null) {
            throw new ApplicationException("Username " + username + " is already taken.", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByEmail(email) != null) {
            throw new ApplicationException("Email " + email + " is already taken.", HttpStatus.BAD_REQUEST);
        }
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(Constants.DEFAULT_ROLE);
        newUser.encodePassword(passwordEncoder); // hashing password
        return userRepository.save(newUser);
    }

    public void addUserReservation(Long userId, Long reservationId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException("User with Id " + userId + " does not exist.", HttpStatus.NOT_FOUND));
        Reservation reservation = AdminService.reservationById(reservationId, reservationRepository, MAX_ROOM_CAPACITY);
        if (!user.getUserHasReservation().contains(reservation)) {
            addToRoom(reservation);
        } else {
            throw new ApplicationException("User already has a reservation in this room.", HttpStatus.BAD_REQUEST);
        }
        user.getUserHasReservation().add(reservation);
        reservationRepository.save(reservation);
        userRepository.save(user);
    }

    public void addUserPayment(Long userId, Long paymentId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException("User with Id " + userId + " does not exist.", HttpStatus.NOT_FOUND));
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + paymentId + " does not exist.", HttpStatus.NOT_FOUND));
        payment.setUserPayments(user);
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long userId, String username, String email, String password) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException("User with Id " + userId + " does not exist.", HttpStatus.NOT_FOUND));
        if (username != null && username.length() > 0 && !Objects.equals(user.getUsername(), username)) {
            user.setUsername(username);
        } else {
            throw new ApplicationException(
                    "User username must be filled completely or this username already set.", HttpStatus.BAD_REQUEST);
        }
        if (email != null && email.length() > 0 && !Objects.equals(user.getEmail(), email)) {
            user.setEmail(email);
        } else {
            throw new ApplicationException(
                    "User email must be filled completely or this email already set.", HttpStatus.BAD_REQUEST);
        }
        if (password != null && password.length() > 0 && !Objects.equals(user.getPassword(), password)) {
            user.setPassword(password);
            user.encodePassword(passwordEncoder); // hashing password
        } else {
            throw new ApplicationException(
                    "User password must be filled completely or this password already set.", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new ApplicationException(
                    "User with id " + userId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    public void deleteReservationFromUser(Long userId, Long reservationId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApplicationException("User with Id " + userId + " does not exist.", HttpStatus.NOT_FOUND));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        if (user.getUserHasReservation().contains(reservation)) {
            removeFromRoom(reservation);
        } else {
            throw new ApplicationException(
                    "User " + user.getUsername()
                            + " already does not have a reservation " +
                            "with id " + reservationId + ".", HttpStatus.BAD_REQUEST);
        }
        user.getUserHasReservation().clear();
        reservationRepository.save(reservation);
        userRepository.save(user);
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

    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
