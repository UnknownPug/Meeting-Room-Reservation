package cvut.fel.ear.room.meeting.repository;

import cvut.fel.ear.room.meeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByIdIsNotNullOrderByUsernameAsc();

    List<User> findAllByIdIsNotNullOrderByUsernameDesc();

    List<User> findAllByUserHasReservationNotNull();

    @Email
    User findByEmail(String email);

    User findByUsername(String username);
}
