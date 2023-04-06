package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.PaymentRepository;
import cvut.fel.ear.room.meeting.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.entity.Reservation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository repository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public PaymentService(PaymentRepository repository, ReservationRepository reservationRepository) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
    }

    public Collection<Payment> getPayments() {
        return repository.findAll();
    }

    public Optional<Payment> getPaymentById(Long paymentId) {
        Payment payment = repository.findById(paymentId).orElseThrow(
                () -> new ApplicationException(
                        "Payment with id " + paymentId + " does not exist.", HttpStatus.NOT_FOUND));
        getTotalPrice(payment);
        return Optional.of(payment);
    }

    public void getTotalPrice(Payment payment) {
        Double totalPrice = 0D;

        if (reservationRepository.findAllByReservationsPaymentNotNull() != null) {
            payment.setTotalPrice(totalPrice);
        }
        payment.setTotalPrice(totalPrice);
        repository.save(payment);
    }

    public Collection<Payment> getPaymentsByDates(LocalDateTime startDate) {
        return repository.findAllByDateOfCreateAfter(startDate);
    }

    public Payment createPayment(Long reservationId) {
        Payment payment = new Payment();
        Reservation reservation = addTotalPrice(reservationId, payment);
        reservation.setReservationsPayment(payment);
        payment.setDateOfCreate(LocalDateTime.now());
        return repository.save(payment);
    }

    private Reservation addTotalPrice(Long reservationId, Payment payment) {
        Double totalPrice = 0D;
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        if (reservationRepository.findAllByReservationsPaymentNotNull() != null) {
            totalPrice += reservation.getPrice();
        }
        payment.setTotalPrice(totalPrice);
        return reservation;
    }

    public void addPaymentReservation(Long id, Long reservationId) {
        Payment payment = repository.findById(id).orElseThrow(
                () -> new ApplicationException(
                        "Payment whit id " + id + " does not exist.", HttpStatus.NOT_FOUND));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ApplicationException(
                        "Reservation with id " + reservationId + " does not exist.", HttpStatus.NOT_FOUND));
        payment.getReservations().add(reservation);
        reservation.setReservationsPayment(payment);
        reservationRepository.save(reservation);
        repository.save(payment);
    }

    public void deletePayment(Long paymentId) {
        boolean exists = repository.existsById(paymentId);
        if (!exists) {
            throw new ApplicationException("Payment with id " + paymentId + " does not exist.", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(paymentId);
    }
}
