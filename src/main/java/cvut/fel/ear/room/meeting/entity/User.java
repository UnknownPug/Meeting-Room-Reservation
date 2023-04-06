package cvut.fel.ear.room.meeting.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user_profile")
public class User extends AbstractUser {

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_has_reservation",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "reservation_id")
    )
    private Set<Reservation> userHasReservation;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userPayments")
    private Set<Payment> payments;


    public Set<Reservation> getUserHasReservation() {
        return userHasReservation;
    }

    public void setUserHasReservation(Set<Reservation> userHasReservation) {
        this.userHasReservation = userHasReservation;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", username=" + getUsername() +
                ", email=" + getEmail() +
                ", password=" + getPassword() +
                ", role=" + getRole() +
                '}';
    }
}
