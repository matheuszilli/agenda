package com.agenda.app.service;


import com.agenda.app.exception.PaymentRequiredException;
import com.agenda.app.model.Item;
import com.agenda.app.model.Payment;
import com.agenda.app.model.PaymentStatus;
import com.agenda.app.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * Grante que caso o seviço exija p're-pagamento, exista um Payment COMPLETED/
     * Se não exigir, devolve null - talvez pensar pra um payment opcional
     */

    @Transactional(readOnly = true)
    public Payment verifyPrePaymentIfWithinWindow(Item item,
                                                  LocalDateTime startTime,
                                                  UUID paymentId) {

        if (!item.isRequiresPrePayment()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = startTime.minusDays(2);
        if (now.isBefore(deadline)) {
            return null;
        }

        if (paymentId == null)
            throw new PaymentRequiredException("Payment is required for this service");

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentRequiredException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED)
            throw new PaymentRequiredException("Payment is not completed");

        return payment;
    }
}
