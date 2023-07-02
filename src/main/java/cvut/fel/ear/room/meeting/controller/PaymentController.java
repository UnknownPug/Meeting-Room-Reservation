package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.dto.request.PaymentRequest;
import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/")
    public ResponseEntity<Iterable<Payment>> getPayments() {
        return ResponseEntity.ok(service.getPayments());
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping(path = "/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("id") long reservationId) {
        if (reservationId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Payment id must be specified.");
        }
        return ResponseEntity.ok(service.getPaymentById(reservationId));
    }


    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(path = "/dates")
    public ResponseEntity<Iterable<Payment>> getPaymentsByDates(@RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(service.getPaymentsByDates(paymentRequest.dateOfCreate())
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest paymentRequest) {
        if (paymentRequest.reservationId() == null) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    "To create a payment, you must specify the reservation id with which it will be associated.");
        }
        return ResponseEntity.ok(service.createPayment(paymentRequest.reservationId()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(path = "/reservation")
    public void addPaymentReservation(@RequestBody PaymentRequest paymentRequest) {
        if (paymentRequest.id() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Payment id must be specified.");
        }
        if (paymentRequest.reservationId() == null) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Reservation id must be specified.");
        }
        service.addPaymentReservation(paymentRequest.id(), paymentRequest.reservationId());
        LOG.debug("Payment with id {} was successfully added to reservation with id {}.",
                paymentRequest.id(), paymentRequest.reservationId()
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(path = "/{id}")
    public void deletePayment(@PathVariable("id") long paymentId) {
        if (paymentId <= 0) {
            throw new ApplicationException(HttpStatus.NOT_FOUND, "Payment id must be specified.");
        }
        service.deletePayment(paymentId);
        LOG.debug("Payment with id {} was successfully deleted.", paymentId);
    }
}