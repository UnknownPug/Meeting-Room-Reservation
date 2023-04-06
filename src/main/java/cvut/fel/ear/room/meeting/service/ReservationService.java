package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.entity.Room;
import cvut.fel.ear.room.meeting.entity.User;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import cvut.fel.ear.room.meeting.repository.RoomRepository;
import cvut.fel.ear.room.meeting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReservationService(ReservationRepository repository, RoomRepository roomRepository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    /*
    • Reservation service for Admin
    */

    public Collection<Reservation> getReservations() {
        Collection<Reservation> reservations = repository.findAll();
        for (Reservation reservation : reservations) {
            addPrice(reservation);
        }
        return reservations;
    }

    public Optional<Reservation> getReservationById(Long reservationId) {
        Reservation reservation = repository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        addPrice(reservation);
        return Optional.of(reservation);
    }

    public Collection<Reservation> getReservationsBetween(LocalDateTime timeStart, LocalDateTime timeEnd) {
        return repository.findReservationsByReservationDateTimeStartBetweenOrReservationDateTimeEndBetween(
                timeStart, timeEnd,
                timeStart, timeEnd
        );
    }

    public void updateReservation(Long reservationId, LocalDateTime reservationDateTimeStart,
                                  LocalDateTime reservationDateTimeEnd) {
        Reservation reservation = repository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND)
        );
        if (reservationDateTimeStart != null &&
                !Objects.equals(reservation.getReservationDateTimeStart(), reservationDateTimeStart) &&
                !reservationDateTimeStart.isBefore(LocalDateTime.now())) {
            reservation.setReservationDateTimeStart(reservationDateTimeStart);
        }
        if (reservationDateTimeEnd != null &&
                !Objects.equals(reservation.getReservationDateTimeEnd(), reservationDateTimeEnd) &&
                !reservationDateTimeEnd.isAfter(LocalDateTime.of(
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().getMonth(),
                        reservationDateTimeEnd.toLocalDate().lengthOfMonth(), 23, 59))) {
            reservation.setReservationDateTimeEnd(reservationDateTimeEnd);
        }
        reservation.setReservationDateTimeStart(reservationDateTimeStart);
        reservation.setReservationDateTimeEnd(reservationDateTimeEnd);
        repository.save(reservation);
    }

    /*
     * Usage for both roles
     */
    public ArrayList<Reservation> getReservationsByNumAsc(Integer num) {
        if (num == null) {
            throw new ApplicationException("Top number should be specified.", HttpStatus.BAD_REQUEST);
        }
        return getReservations(repository.findAllByIdIsNotNullOrderByPriceAsc(), num);
    }

    public ArrayList<Reservation> getReservationsByNumDesc(Integer num) {
        if (num == null) {
            throw new ApplicationException("Top number should be specified.", HttpStatus.BAD_REQUEST);
        }
        return getReservations(repository.findAllByIdIsNotNullOrderByPriceDesc(), num);
    }

    private ArrayList<Reservation> getReservations(ArrayList<Reservation> reservations, Integer num) {
        ArrayList<Reservation> printReservations = new ArrayList<>();
        if (num > repository.count()) {
            throw new ApplicationException(
                    "Top number can be maximum " + repository.count() + ".", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < num; i++) {
            printReservations.add(reservations.get(i));
        }
        return printReservations;
    }

    public Collection<Reservation> getReservationsStart(LocalDateTime timeStart) {
        if (timeStart == null) {
            throw new ApplicationException("Start time must be set.", HttpStatus.BAD_REQUEST);
        }
        return repository.findAllByReservationDateTimeStartAfter(timeStart);
    }

    public Collection<Reservation> getReservationsEnd(LocalDateTime timeEnd) {
        if (timeEnd == null) {
            throw new ApplicationException("End time must be set.", HttpStatus.BAD_REQUEST);
        }
        return repository.findAllByReservationDateTimeEndBefore(timeEnd);
    }

    public Reservation createReservation(LocalDateTime reservationDateTimeStart,
                                         LocalDateTime reservationDateTimeEnd) {
        Reservation reservation = new Reservation();
        if (reservationDateTimeStart.isBefore(LocalDateTime.now())) {
            throw new DateTimeException("Time you set is less than current time.");
        }
        if (reservationDateTimeEnd.isAfter(LocalDateTime.of(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                reservationDateTimeEnd.toLocalDate().lengthOfMonth(), 23, 59))) {
            throw new DateTimeException("Time you set is more than current time.");
        }
        reservation.setPrice(0D);
        reservation.setReservationDateTimeStart(reservationDateTimeStart);
        reservation.setReservationDateTimeEnd(reservationDateTimeEnd);
        return repository.save(reservation);
    }

    /*
    • Reservation service for User
    */

    public void addReservationRoom(Long reservationId, Long roomId) {
        Reservation reservation = repository.findById(reservationId)
                .orElseThrow(
                        () -> new ApplicationException(
                                "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new ApplicationException(
                                "Reservation with id " + roomId + " does not exist.", HttpStatus.NOT_FOUND));
        if (reservation.getReservationsPayment() == null) {
            throw new ApplicationException("Room cannot be added until payment is not set.", HttpStatus.BAD_REQUEST);
        }
        reservation.setRoomReservation(room);
        addPrice(reservation);
        repository.save(reservation);
    }

    public void addPrice(Reservation reservation) {
        Room room = reservation.getRoomReservation();
        if (room == null) {
            reservation.setPrice(0D);
        } else {
            double price;
            int deltaHour = reservation.getReservationDateTimeStart().getHour() -
                    reservation.getReservationDateTimeEnd().getHour();
            int deltaMinute = reservation.getReservationDateTimeEnd().getMinute() -
                    reservation.getReservationDateTimeStart().getMinute();
            if (deltaHour > 0) {
                deltaHour = 24 - (deltaHour);
            } else {
                deltaHour = abs(deltaHour);
            }
            int inMinutes = deltaHour * 60 + deltaMinute;
            price = (double) inMinutes / 60 * room.getPricePerHour();
            reservation.setPrice(price);
            repository.save(reservation);
        }
    }

    public void deleteReservation(Long reservationId) {
        boolean exists = repository.existsById(reservationId);
        if (!exists) {
            throw new ApplicationException(
                    "Reservation with id " + reservationId + " does not exist.", HttpStatus.BAD_REQUEST);
        }
        // Check if there are any reservations for the user
        Set<Long> userReservedRoomIds = userRepository.findAllByUserHasReservationNotNull()
                .stream()
                .map(User::getUserHasReservation)
                .flatMap(Set::stream) // flatten to stream of reservations
                .map(Reservation::getId)
                .collect(Collectors.toSet());
        if (userReservedRoomIds.contains(reservationId)) {
            throw new ApplicationException("Reservation with id " + reservationId + " cannot be deleted " +
                    "because it is controlled by user.", HttpStatus.BAD_REQUEST);
        }
        repository.deleteById(reservationId);
    }
}
