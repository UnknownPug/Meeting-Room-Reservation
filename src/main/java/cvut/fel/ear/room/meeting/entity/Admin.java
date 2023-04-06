package cvut.fel.ear.room.meeting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "admin_profile")
public class Admin extends AbstractUser {

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "admin_control_room",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private Set<Room> adminControlRoom;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "admin_has_reservation",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id")
    )
    private Set<Reservation> adminHasReservation;

    public Set<Room> getAdminControlRoom() {
        return adminControlRoom;
    }

    public void setAdminControlRoom(Set<Room> adminControlRoom) {
        this.adminControlRoom = adminControlRoom;
    }

    public Set<Reservation> getAdminReservations() {
        return adminHasReservation;
    }

    public void setAdminReservations(Set<Reservation> adminReservations) {
        this.adminHasReservation = adminReservations;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getId() +
                ", username=" + getUsername() +
                ", email=" + getEmail() +
                ", password=" + getPassword() +
                '}';
    }
}
