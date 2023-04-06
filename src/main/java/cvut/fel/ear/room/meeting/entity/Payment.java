package cvut.fel.ear.room.meeting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NamedQuery(name = "Payment.findAllByDateOfCreateBetween",
        query = "SELECT p FROM Payment p WHERE p.dateOfCreate " +
                "BETWEEN :startDate AND :endDate " +
                "ORDER BY p.dateOfCreate DESC"
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    @Column(name = "date_of_create", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateOfCreate;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reservationsPayment")
    private Set<Reservation> reservations;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_payments")
    private User userPayments;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public LocalDateTime getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(LocalDateTime dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public User getUserPayments() {
        return userPayments;
    }

    public void setUserPayments(User userPayments) {
        this.userPayments = userPayments;
    }

    @Override
    public String toString() {
        return "Payment{" + "id=" + id + ", totalPrice=" + totalPrice + ", dateCreates=" + dateOfCreate + '}';
    }
}
