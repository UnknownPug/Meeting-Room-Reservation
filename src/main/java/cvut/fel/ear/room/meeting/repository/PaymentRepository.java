package cvut.fel.ear.room.meeting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import cvut.fel.ear.room.meeting.entity.Payment;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query(name = "Payment.findAllByDateOfCreateAfter")
    Set<Payment> findAllByDateOfCreateAfter(@Param("startDate") LocalDateTime startDate);
}
