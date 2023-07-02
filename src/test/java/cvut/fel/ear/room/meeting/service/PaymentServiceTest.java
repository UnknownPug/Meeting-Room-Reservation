package cvut.fel.ear.room.meeting.service;

import cvut.fel.ear.room.meeting.entity.Payment;
import cvut.fel.ear.room.meeting.exception.ApplicationException;
import cvut.fel.ear.room.meeting.repository.PaymentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment payment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        payment = new Payment();
        payment.setId(1L);
    }

    @Test
    public void testGetPaymentsReturnsValidPaymentsList() {
        List<Payment> payments = new ArrayList<>();
        payments.add(payment);
        when(paymentRepository.findAll()).thenReturn(payments);
        Collection<Payment> foundPayments = paymentService.getPayments();
        assertNotNull(foundPayments);
    }

    @Test(expected = ApplicationException.class)
    public void getPaymentByIdWhenNotFoundThrowsApplicationException() {
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());
        paymentService.getPaymentById(payment.getId());
    }
}
