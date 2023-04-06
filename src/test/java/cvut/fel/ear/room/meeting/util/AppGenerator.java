package cvut.fel.ear.room.meeting.util;

import cvut.fel.ear.room.meeting.entity.*;

import java.time.LocalDateTime;
import java.util.Random;

public class AppGenerator {

    private static AppGenerator INSTANCE;

    public static AppGenerator getInstance(){
        if (INSTANCE == null)
            INSTANCE = new AppGenerator();
        return INSTANCE;
    }

    private final Random RAND = new Random();

    public Long randomLong() {
        return RAND.nextLong();
    }

    public Admin generateAdmin() {
        final Admin admin = new Admin();
        admin.setEmail("test@admint.com");
        admin.setPassword(String.valueOf("123123".hashCode()));
        admin.setUsername("AdminT");
        admin.setRole(Role.ADMIN);
        return admin;
    }

    public User generateUser() {
        final User user = new User();
        user.setEmail("user@usert.com");
        user.setPassword(String.valueOf("123123123".hashCode()));
        user.setUsername("UserT");
        return user;
    }

    public Room genereateRoom() {
        final Room room = new Room();
        room.setDateOfCreate(LocalDateTime.now());
        room.setText("YES");
        room.setName("Room 69");
        room.setPricePerHour(230D);
        room.setRoomCapacity(0);
        return room;
    }

    public Payment generatePayment() {
        final Payment payment = new Payment();
        payment.setDateOfCreate(LocalDateTime.now());
        payment.setTotalPrice(22300D);
        payment.setUserPayments(generateUser());
        return payment;
    }

    public Reservation generateReservation() {
        final Reservation reservation = new Reservation();
        reservation.setPrice(100D);
        reservation.setReservationDateTimeEnd(LocalDateTime.MAX);
        reservation.setReservationDateTimeStart(LocalDateTime.MIN);
        reservation.setReservationsPayment(generatePayment());
        reservation.setRoomReservation(genereateRoom());
        return reservation;
    }
}
