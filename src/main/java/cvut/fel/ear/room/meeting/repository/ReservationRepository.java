package cvut.fel.ear.room.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import cvut.fel.ear.room.meeting.entity.Reservation;

import javax.persistence.OrderBy;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(name = "Reservation.findReservationsByReservationDateTimeStartBetweenOrReservationDateTimeEndBetween")
    Set<Reservation> findReservationsByReservationDateTimeStartBetweenOrReservationDateTimeEndBetween(

            @Param("reservationDateTimeStart") LocalDateTime reservationDateTimeStart,
            @Param("reservationDateTimeStart2") LocalDateTime reservationDateTimeStart2,

            @Param("reservationDateTimeEnd") LocalDateTime reservationDateTimeEnd,
            @Param("reservationDateTimeEnd2") LocalDateTime reservationDateTimeEnd2
    );

    List<Reservation> findAllByIdIsNotNullOrderByPriceAsc();

    List<Reservation> findAllByIdIsNotNullOrderByPriceDesc();

    Boolean existsByRoomReservationId(Long roomId);

    @OrderBy("timeStart ASC")
    List<Reservation> findAllByReservationDateTimeStartAfter(LocalDateTime timeStart);

    @OrderBy("timeStart DESC")
    List<Reservation> findAllByReservationDateTimeEndBefore(LocalDateTime timeEnd);

    @Query(name = "SELECT SUM(r.price) FROM Reservation r")
    List<Reservation> findAllByReservationsPaymentNotNull();
}
