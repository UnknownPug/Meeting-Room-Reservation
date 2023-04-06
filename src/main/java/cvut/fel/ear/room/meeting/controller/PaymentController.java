package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.PaymentRequest;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.service.PaymentService;

import java.util.Optional;

@RestController
@RequestMapping(path = "/payment")
public class PaymentController {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService service;

    @Autowired
    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list")
    public ResponseEntity<Iterable<Payment>> getPayments() {
        return ResponseEntity.ok(service.getPayments());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/{id}")
    public ResponseEntity<Optional<Payment>> getPaymentById(@PathVariable("id") Long reservationId) {
        if (reservationId == null) {
            throw new ApplicationException("Payment does not found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(service.getPaymentById(reservationId));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/list/dates")
    public ResponseEntity<Iterable<Payment>> getPaymentsByDates(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(service.getPaymentsByDates(paymentRequest.dateOfCreate())
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        if (paymentRequest.reservationId() == null) {
            throw new ApplicationException("Payment must be created.", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(service.createPayment(paymentRequest.reservationId()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/reservation")
    public void addPaymentReservation(@RequestBody PaymentRequest paymentRequest) {
        if (paymentRequest.id() == null) {
            throw new ApplicationException("Payment does not found.", HttpStatus.NOT_FOUND);
        }
        if (paymentRequest.reservationId() == null) {
            throw new ApplicationException("Reservation does not found.", HttpStatus.NOT_FOUND);
        }
        service.addPaymentReservation(paymentRequest.id(), paymentRequest.reservationId());
        LOG.debug(
                "Payment {} successfully added to reservation {}.", paymentRequest.id(), paymentRequest.reservationId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deletePayment(@PathVariable("id") Long paymentId) {
        if (paymentId == null) {
            throw new ApplicationException("Payment does not found.", HttpStatus.NOT_FOUND);
        }
        service.deletePayment(paymentId);
        LOG.debug("Payment {} successfully deleted.", paymentId);
    }
}