package de.htw.paymentservice.core.domain.service.interfaces;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import de.htw.paymentservice.port.dto.BasketDTO;

public interface IStripeService {
    Session createCheckoutSession(BasketDTO basket) throws StripeException;
    String retrieveCheckoutStatus(String sessionId) throws StripeException;

    void expireSession(String sessionId) throws StripeException;
}
