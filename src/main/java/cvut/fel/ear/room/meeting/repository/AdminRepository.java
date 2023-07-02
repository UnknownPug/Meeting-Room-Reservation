package cvut.fel.ear.room.meeting.repository;

import cvut.fel.ear.room.meeting.entity.Admin;
import cvut.fel.ear.room.meeting.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findAllByIdIsNotNullOrderByUsernameAsc();

    List<Admin> findAllByIdIsNotNullOrderByUsernameDesc();

    Admin findByUsername(String username);

    Admin findByEmail(String email);

    Boolean findAllByEmail(String email);

    Boolean findAllByUsername(String username);

    Boolean existsByAdminControlRoomContains(Room one);
}
