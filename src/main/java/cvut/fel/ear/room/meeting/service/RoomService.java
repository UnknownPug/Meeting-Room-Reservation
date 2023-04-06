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
            throw new ApplicationException("Room price must be set.", HttpStatus.BAD_REQUEST);
        }
        if (!rooms.isEmpty()) {
            throw new ApplicationException("Room with name " + name + " is already taken.", HttpStatus.BAD_REQUEST);
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
                () -> new ApplicationException("Room with id " + roomId + " does not exist.", HttpStatus.NOT_FOUND));
        if (name != null && !Objects.equals(room.getName(), name)) {
            room.setName(name);
            room.setPricePerHour(pricePerHour);
        } else {
            throw new ApplicationException("Room name already set or not filled.", HttpStatus.BAD_REQUEST);
        }
        if (description != null && description.length() <= 20L) {
            room.setText(description);
        } else {
            throw new ApplicationException(
                    "Description must be filled and can contain lup to 20 characters.", HttpStatus.BAD_REQUEST);
        }
        repository.save(room);
    }

    public void deleteRoom(Long roomId) {
        boolean exists = repository.existsById(roomId);
        if (!exists) {
            throw new ApplicationException("Room with id " + roomId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        // Check if there are any reservations for the room
        boolean roomHasReservation = reservationRepository.findAllByRoomReservationNotNull()
                .stream()
                .anyMatch(reservation -> reservation.getRoomReservation().getId().equals(roomId));
        if (roomHasReservation) {
            throw new ApplicationException(
                    "Room with id " + roomId + " cannot be deleted " +
                            "because it is reserved by user.", HttpStatus.BAD_REQUEST);
        }
        // Check if there are any admins controlling the room
        boolean roomIsControlledByAdmin = adminRepository.findAllByAdminControlRoomNotNull()
                .stream()
                .map(Admin::getAdminControlRoom)
                .anyMatch(roomSet -> roomSet.contains(repository.getOne(roomId)));
        if (roomIsControlledByAdmin) {
            throw new ApplicationException(
                    "Room with id " + roomId + " cannot be deleted " +
                            "because it is controlled by one or more admins.", HttpStatus.BAD_REQUEST);
        }
        repository.deleteById(roomId);
    }

    public Room getRoomByName(String name) {
        if (repository.findByName(name) == null) {
            throw new ApplicationException("Name does not exist.", HttpStatus.NOT_FOUND);
        }
        return repository.findByName(name);
    }

    public ArrayList<Room> getRoomsByNumAsc(Integer num) {
        return getRoomsNum(repository.findAllByIdIsNotNullOrderByPricePerHourAsc(), num);
    }

    public ArrayList<Room> getRoomsByNumDesc(Integer num) {
        return getRoomsNum(repository.findAllByIdIsNotNullOrderByPricePerHourDesc(), num);
    }

    public ArrayList<Room> getRoomsByCapacityAsc(Integer num) {
        return getRoomsNum(repository.findAllByIdIsNotNullOrderByRoomCapacityAsc(), num);
    }

    private ArrayList<Room> getRoomsNum(ArrayList<Room> rooms, Integer num) {
        ArrayList<Room> printRooms = new ArrayList<>();
        if (num > repository.count()) {
            throw new ApplicationException(
                    "Maximum top number is: " + repository.count() + ".", HttpStatus.BAD_REQUEST);
        }
        for (int i = 0; i < num; i++) {
            printRooms.add(rooms.get(i));
        }
        return printRooms;
    }
}
