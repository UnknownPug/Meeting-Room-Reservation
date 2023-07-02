package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Reservation;
import cvut.fel.ear.room.meeting.entity.Room;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.AdminRepository;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import cvut.fel.ear.room.meeting.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RoomService {

    private final ReservationRepository reservationRepository;

    private final RoomRepository repository;

    private final AdminRepository adminRepository;

    @Autowired
    public RoomService(ReservationRepository reservationRepository,
                       RoomRepository repository, AdminRepository adminRepository1) {
        this.reservationRepository = reservationRepository;
        this.repository = repository;
        this.adminRepository = adminRepository1;
    }

    public Room getRoomById(Long roomId) {
        return repository.findById(roomId).orElseThrow(
                () -> new ApplicationException(
                        HttpStatus.NOT_FOUND, "Room with id " + roomId + " does not exist."));
    }

    public Collection<Room> getRooms() {
        return repository.findAll();
    }

    public Collection<Room> getFreeRooms() {
        return repository.findRoomsByReservationsIsNull();
    }

    public Collection<Room> getFreeRoomsBetweenTime(LocalDateTime startTime, LocalDateTime endTime) {
        Set<Room> availableRooms = repository.findAll();
        Set<Reservation> reservations = reservationRepository.
                findReservationsByReservationDateTimeStartBetweenOrReservationDateTimeEndBetween(
                        startTime, endTime,
                        startTime, endTime);
        for (Reservation reservation : reservations) {
            availableRooms.remove(reservation.getRoomReservation());
        }
        return availableRooms;
    }

    public Room createRoom(String name, Double pricePerHour, String description) {
        Room newRoom = new Room();
        List<Room> rooms = repository.findAllByName(name);
        if (pricePerHour == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room price must be set.");
        }
        if (!rooms.isEmpty()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room with name " + name + " is already taken.");
        }
        newRoom.setRoomCapacity(0);
        newRoom.setName(name);
        newRoom.setPricePerHour(pricePerHour);
        newRoom.setText(description);
        newRoom.setDateOfCreate(LocalDateTime.now());
        return repository.save(newRoom);
    }

    public void updateRoom(Long roomId, String name, Double pricePerHour, String description) {
        Room room = repository.findById(roomId).orElseThrow(
                () -> new ApplicationException(HttpStatus.NOT_FOUND, "Room with id " + roomId + " does not exist."));
        if (name != null && !Objects.equals(room.getName(), name)) {
            room.setName(name);
            room.setPricePerHour(pricePerHour);
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Room name already set or not filled.");
        }
        if (description != null && description.length() <= 20L) {
            room.setText(description);
        } else {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Description must be filled and can contain lup to 20 characters.");
        }
        repository.save(room);
    }

    public void deleteRoom(Long roomId) {
        boolean exists = repository.existsById(roomId);
        if (!exists) {
            throw new ApplicationException(HttpStatus.NOT_FOUND,
                    "Room with id " + roomId + " does not exist.");
        }
        // Check if there are any reservations for the room
        boolean roomHasReservation = reservationRepository.existsByRoomReservationId(roomId);
        if (roomHasReservation) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Room with id " + roomId + " cannot be deleted because it is reserved by a user.");
        }
        // Check if there are any admins controlling the room
        boolean roomIsControlledByAdmin = adminRepository.existsByAdminControlRoomContains(repository.getOne(roomId));
        if (roomIsControlledByAdmin) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "Room with id " + roomId + " cannot be deleted " +
                            "because it is controlled by one or more admins.");
        }
        repository.deleteById(roomId);
    }

    public Room getRoomByName(String name) {
        if (repository.findByName(name) == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Name does not exist.");
        }
        return repository.findByName(name);
    }

    public List<Room> getRoomsByNumAsc(Integer num) {
        Pageable pageable = PageRequest.of(0, num);
        List<Room> rooms = repository.findTopNByPricePerHourAsc(pageable);
        if (rooms.size() < num) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Maximum top number is: " + rooms.size() + ".");
        }
        return rooms;
    }

    public List<Room> getRoomsByNumDesc(Integer num) {
        Pageable pageable = PageRequest.of(0, num);
        List<Room> rooms = repository.findTopNByPricePerHourDesc(pageable);
        if (rooms.size() < num) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Maximum top number is: " + rooms.size() + ".");
        }
        return rooms;
    }

    public List<Room> getRoomsByCapacityAsc(Integer num) {
        Pageable pageable = PageRequest.of(0, num);
        List<Room> rooms = repository.findTopNByRoomCapacityAsc(pageable);
        if (rooms.size() < num) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST, "Maximum top number is: " + rooms.size() + ".");
        }
        return rooms;
    }
}
