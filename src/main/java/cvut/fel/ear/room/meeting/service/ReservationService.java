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
        return repository.findAll();
    }

    public Reservation getReservationById(Long reservationId) {
        return repository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Reservation with id " + reservationId + " does not exist."));
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
                        HttpStatus.NOT_FOUND, "Reservation with id " + reservationId + " does not exist.")
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
    public List<Reservation> getReservationsByNumAsc(int num) {
        return getReservations(repository.findAllByIdIsNotNullOrderByPriceAsc(), num);
    }

    public List<Reservation> getReservationsByNumDesc(Integer num) {
        return getReservations(repository.findAllByIdIsNotNullOrderByPriceDesc(), num);
    }

    private List<Reservation> getReservations(List<Reservation> reservations, Integer num) {
        List<Reservation> printReservations = new ArrayList<>();
        if (num > repository.count()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Top number can be maximum " + repository.count() + ".");
        }
        for (int i = 0; i < num; i++) {
            printReservations.add(reservations.get(i));
        }
        return printReservations;
    }

    public Collection<Reservation> getReservationsStart(LocalDateTime timeStart) {
        if (timeStart == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Start time must be set.");
        }
        return repository.findAllByReservationDateTimeStartAfter(timeStart);
    }

    public Collection<Reservation> getReservationsEnd(LocalDateTime timeEnd) {
        if (timeEnd == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "End time must be set.");
        }
        return repository.findAllByReservationDateTimeEndBefore(timeEnd);
    }

    public Reservation createReservation(LocalDateTime reservationDateTimeStart,
                                         LocalDateTime reservationDateTimeEnd) {
        Reservation reservation = new Reservation();
        if (reservationDateTimeStart.isBefore(LocalDateTime.now())) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "You cannot select a date and time that has already passed.");
        }
        if (reservationDateTimeEnd.isAfter(LocalDateTime.of(
                LocalDateTime.now().getYear(),
                LocalDateTime.now().getMonth(),
                reservationDateTimeEnd.toLocalDate().lengthOfMonth(), 23, 59))) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "You cannot select a date and time that is greater" +
                            " than the maximum date and time of the current month.");
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
                        () -> new ApplicationException(HttpStatus.NOT_FOUND,
                                "Reservation with id " + reservationId + " does not exist."));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new ApplicationException(
                                HttpStatus.NOT_FOUND, "Reservation with id " + roomId + " does not exist."));
        if (reservation.getReservationsPayment() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room cannot be added until payment is not set.");
        }
        reservation.setRoomReservation(room);
        addPriceToReservation(reservation);
        repository.save(reservation);
    }

    public void addPriceToReservation(Reservation reservation) {
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
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Reservation with id " + reservationId + " does not exist.");
        }
        // Check if there are any reservations for the user
        Set<Long> userReservedRoomIds = userRepository.findAllByUserHasReservationNotNull()
                .stream()
                .map(User::getUserHasReservation)
                .flatMap(Set::stream) // flatten to stream of reservations
                .map(Reservation::getId)
                .collect(Collectors.toSet());
        if (userReservedRoomIds.contains(reservationId)) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Reservation with id " + reservationId + " cannot be deleted " +
                            "because it is controlled by user.");
        }
        repository.deleteById(reservationId);
    }
}
