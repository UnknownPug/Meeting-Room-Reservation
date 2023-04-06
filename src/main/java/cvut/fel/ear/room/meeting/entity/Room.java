package cvut.fel.ear.room.meeting.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    @Size(max = 20, min = 4, message = "Room name is less than 4 or bigger than 20 letters can't exist.")
    private String name;


    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;

    @Column(name = "text", nullable = false)
    @Size(max = 20, message = "Description can contain maximum 20 letters.")
    private String text;

    @Column(name = "date_of_create", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm", iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateOfCreate;

    @Column(name = "room_capacity", nullable = false)
    private Integer roomCapacity;

    @JsonIgnore
    @ManyToMany(mappedBy = "adminControlRoom")
    private Set<Admin> adminRooms;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "roomReservation")
    private Set<Reservation> reservations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(LocalDateTime dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }

    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public Set<Admin> getAdminRooms() {
        return adminRooms;
    }

    public void setAdminRooms(Set<Admin> adminRooms) {
        this.adminRooms = adminRooms;
    }


    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id +
                ", name='" + name + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", description='" + text + '\'' +
                ", dateCreates=" + dateOfCreate + '\'' +
                ", roomCapacity=" + roomCapacity + '\'' +
                '}';
    }
}
