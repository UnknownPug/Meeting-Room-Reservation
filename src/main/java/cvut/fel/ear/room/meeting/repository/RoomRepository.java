package cvut.fel.ear.room.meeting.repository;

import cvut.fel.ear.room.meeting.entity.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    Set<Room> findRoomsByReservationsIsNull();

    @SuppressWarnings("NullableProblems")
    Set<Room> findAll();

    Room findByName(String name);

    ArrayList<Room> findAllByIdIsNotNullOrderByRoomCapacityAsc();

    ArrayList<Room> findAllByIdIsNotNullOrderByPricePerHourAsc();

    ArrayList<Room> findAllByIdIsNotNullOrderByPricePerHourDesc();

    List<Room> findAllByName(String name);

    Room getOne(Long roomId);
}