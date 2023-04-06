package cvut.fel.ear.room.meeting.controller;

import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetPayments() {
        List<Payment> payments = new ArrayList<>();
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setTotalPrice(100.0);
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setTotalPrice(200.0);
        payments.add(payment1);
        payments.add(payment2);

        when(paymentService.getPayments()).thenReturn(payments);

        ResponseEntity<Iterable<Payment>> response = paymentController.getPayments();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<Payment>) response.getBody()).size());
    }

    @Test
    public void testGetPaymentById() {
        Long paymentId = 1L;
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setTotalPrice(100.0);

        when(paymentService.getPaymentById(paymentId)).thenReturn(Optional.of(payment));

        ResponseEntity<Optional<Payment>> response = paymentController.getPaymentById(paymentId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentId, response.getBody().get().getId());
    }

    @Test
    public void testGetPaymentsByDates() {
        Payment payment = new Payment();
        payment.setDateOfCreate(LocalDateTime.now());

        List<Payment> payments = new ArrayList<>();
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setTotalPrice(100.0);
        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setTotalPrice(200.0);
        payments.add(payment1);
        payments.add(payment2);

        when(paymentService.getPaymentsByDates(payment.getDateOfCreate())).thenReturn(payments);
    }

}
