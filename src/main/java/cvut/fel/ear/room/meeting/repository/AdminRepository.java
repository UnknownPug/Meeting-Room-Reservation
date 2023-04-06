package cvut.fel.ear.room.meeting.repository;

import cvut.fel.ear.room.meeting.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findAllByIdIsNotNullOrderByUsernameAsc();

    List<Admin> findAllByIdIsNotNullOrderByUsernameDesc();

    List<Admin> findAllByAdminControlRoomNotNull();

    Admin findByUsername(String username);

    Admin findByEmail(String email);

    boolean findAllByEmail(String email);

    boolean findAllByUsername(String username);
}
