package cvut.fel.ear.room.meeting.repository;

import cvut.fel.ear.room.meeting.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    Set<Room> findRoomsByReservationsIsNull();

    @SuppressWarnings("NullableProblems")
    Set<Room> findAll();

    Room findByName(String name);

    List<Room> findAllByName(String name);

    Room getOne(Long roomId);

    @Query("SELECT r FROM Room r WHERE r.id <= (SELECT MAX(r2.id) FROM Room r2) ORDER BY r.pricePerHour ASC")
    List<Room> findTopNByPricePerHourAsc(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.id <= (SELECT MAX(r2.id) FROM Room r2) ORDER BY r.pricePerHour DESC")
    List<Room> findTopNByPricePerHourDesc(Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.id <= (SELECT MAX(r2.id) FROM Room r2) ORDER BY r.roomCapacity ASC")
    List<Room> findTopNByRoomCapacityAsc(Pageable pageable);


}